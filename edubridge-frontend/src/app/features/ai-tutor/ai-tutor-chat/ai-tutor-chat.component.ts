import { Component, OnInit, OnDestroy, ViewChild, ElementRef, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { AiTutorService, Message, ConversationContext } from '../../../core/services/ai-tutor.service';
import { VoiceIntegrationService } from '../../../core/services/voice-integration.service';
import { MarkdownPipe } from '../../../shared/pipes/markdown.pipe';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-ai-tutor-chat',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MarkdownPipe
  ],
  templateUrl: './ai-tutor-chat.component.html',
  styleUrls: ['./ai-tutor-chat.component.scss']
})
export class AiTutorChatComponent implements OnInit, OnDestroy {
  @ViewChild('messagesContainer') messagesContainer!: ElementRef;
  @ViewChild('messageInput') messageInput!: ElementRef;

  messages = signal<Message[]>([]);
  isTyping = signal<boolean>(false);
  isListening = signal<boolean>(false);
  isSpeaking = signal<boolean>(false);
  userInput = '';

  private subscriptions: Subscription[] = [];
  private autoScroll = true;

  // Quick action suggestions
  quickActions = [
    { icon: 'lightbulb', text: 'Explain this concept', action: 'explain' },
    { icon: 'code', text: 'Show me an example', action: 'example' },
    { icon: 'quiz', text: 'Give me practice', action: 'practice' },
    { icon: 'summarize', text: 'Summarize key points', action: 'summary' },
    { icon: 'tips_and_updates', text: 'Study tips', action: 'tips' }
  ];

  constructor(
    private aiTutorService: AiTutorService,
    private voiceService: VoiceIntegrationService
  ) {
    // Auto-scroll effect
    effect(() => {
      if (this.autoScroll && this.messages().length > 0) {
        setTimeout(() => this.scrollToBottom(), 100);
      }
    });
  }

  ngOnInit(): void {
    // Subscribe to messages
    const messagesSub = this.aiTutorService.messages$.subscribe(messages => {
      this.messages.set(messages);
    });

    // Subscribe to typing indicator
    const typingSub = this.aiTutorService.isTyping$.subscribe(isTyping => {
      this.isTyping.set(isTyping);
    });

    this.subscriptions.push(messagesSub, typingSub);

    // Start a new chat if not already started
    if (this.messages().length === 0) {
      this.aiTutorService.startNewChat();
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.stopVoice();
  }

  async sendMessage(): Promise<void> {
    if (!this.userInput.trim()) return;

    const message = this.userInput;
    this.userInput = '';

    await this.aiTutorService.sendMessage(message);
  }

  async handleQuickAction(action: string): Promise<void> {
    switch (action) {
      case 'explain':
        this.userInput = 'Can you explain this concept in detail?';
        break;
      case 'example':
        this.userInput = 'Can you show me a practical example?';
        break;
      case 'practice':
        this.userInput = 'Give me a practice problem to solve.';
        break;
      case 'summary':
        this.userInput = 'Please summarize the key points.';
        break;
      case 'tips':
        this.userInput = 'What are the best tips to master this?';
        break;
    }

    await this.sendMessage();
  }

  toggleVoiceInput(): void {
    if (this.isListening()) {
      this.stopListening();
    } else {
      this.startListening();
    }
  }

  startListening(): void {
    if (!this.voiceService.isSpeechRecognitionSupported()) {
      alert('Speech recognition is not supported in your browser.');
      return;
    }

    this.isListening.set(true);

    const recognition$ = this.voiceService.startListening();
    const sub = recognition$.subscribe({
      next: (transcript) => {
        this.userInput = transcript;
        this.isListening.set(false);
        // Optionally auto-send
        this.sendMessage();
      },
      error: (error) => {
        console.error('Speech recognition error:', error);
        this.isListening.set(false);
      },
      complete: () => {
        this.isListening.set(false);
      }
    });

    this.subscriptions.push(sub);
  }

  stopListening(): void {
    this.voiceService.stopListening();
    this.isListening.set(false);
  }

  async speakMessage(message: Message): Promise<void> {
    if (this.isSpeaking()) {
      this.stopVoice();
      return;
    }

    if (!this.voiceService.isSpeechSynthesisSupported()) {
      alert('Text-to-speech is not supported in your browser.');
      return;
    }

    this.isSpeaking.set(true);

    try {
      await this.voiceService.speak({
        text: message.content,
        lang: 'en-US',
        rate: 1.0,
        pitch: 1.0,
        volume: 1.0
      });
    } catch (error) {
      console.error('Speech synthesis error:', error);
    } finally {
      this.isSpeaking.set(false);
    }
  }

  stopVoice(): void {
    this.voiceService.stopSpeaking();
    this.voiceService.stopListening();
    this.isSpeaking.set(false);
    this.isListening.set(false);
  }

  clearChat(): void {
    if (confirm('Are you sure you want to clear the conversation?')) {
      this.aiTutorService.startNewChat();
    }
  }

  private scrollToBottom(): void {
    try {
      if (this.messagesContainer) {
        const element = this.messagesContainer.nativeElement;
        element.scrollTop = element.scrollHeight;
      }
    } catch (err) {
      console.error('Scroll error:', err);
    }
  }

  onScroll(): void {
    if (this.messagesContainer) {
      const element = this.messagesContainer.nativeElement;
      const atBottom = element.scrollHeight - element.scrollTop === element.clientHeight;
      this.autoScroll = atBottom;
    }
  }

  formatMessageTime(timestamp: Date): string {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
  }

  isAIAvailable(): boolean {
    return this.aiTutorService.isAvailable();
  }

  handleKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }
}
