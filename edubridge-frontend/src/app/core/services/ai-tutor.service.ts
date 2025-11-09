import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { GoogleGenerativeAI, GenerativeModel, ChatSession } from '@google/generative-ai';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Message {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  timestamp: Date;
  isTyping?: boolean;
  audioUrl?: string;
}

export interface ConversationContext {
  courseId?: string;
  courseName?: string;
  moduleId?: string;
  moduleName?: string;
  topic?: string;
  userLevel?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AiTutorService {
  private genAI: GoogleGenerativeAI | null = null;
  private model: GenerativeModel | null = null;
  private chatSession: ChatSession | null = null;
  private isBrowser: boolean;

  private messagesSubject = new BehaviorSubject<Message[]>([]);
  public messages$ = this.messagesSubject.asObservable();

  private isTypingSubject = new BehaviorSubject<boolean>(false);
  public isTyping$ = this.isTypingSubject.asObservable();

  private conversationContext: ConversationContext = {};

  constructor(@Inject(PLATFORM_ID) platformId: Object) {
    this.isBrowser = isPlatformBrowser(platformId);

    if (this.isBrowser && environment.geminiApiKey) {
      this.initializeAI();
    }
  }

  private initializeAI(): void {
    try {
      this.genAI = new GoogleGenerativeAI(environment.geminiApiKey);

      // Use Gemini 1.5 Flash model (FREE tier)
      this.model = this.genAI.getGenerativeModel({
        model: 'gemini-1.5-flash',
        generationConfig: {
          temperature: 0.7,
          topK: 40,
          topP: 0.95,
          maxOutputTokens: 2048,
        },
      });

      // Initialize chat with system instructions
      this.startNewChat();
    } catch (error) {
      console.error('Failed to initialize Gemini AI:', error);
    }
  }

  private getSystemPrompt(): string {
    const context = this.conversationContext;
    let prompt = `You are an expert AI tutor named "EduBridge AI Assistant". Your role is to help students learn effectively through:

1. Clear, patient explanations tailored to the student's level
2. Breaking down complex topics into simple, digestible parts
3. Using examples, analogies, and real-world applications
4. Asking questions to check understanding
5. Providing encouragement and positive reinforcement
6. Adapting your teaching style based on student responses

Guidelines:
- Be warm, friendly, and encouraging
- Use simple language and avoid jargon unless necessary
- Provide step-by-step explanations
- Check for understanding before moving forward
- Offer multiple ways to explain the same concept
- Celebrate progress and effort
- Be patient with mistakes and use them as learning opportunities`;

    if (context.courseName) {
      prompt += `\n\nCurrent Course: ${context.courseName}`;
    }
    if (context.moduleName) {
      prompt += `\nCurrent Module: ${context.moduleName}`;
    }
    if (context.topic) {
      prompt += `\nCurrent Topic: ${context.topic}`;
    }
    if (context.userLevel) {
      prompt += `\nStudent Level: ${context.userLevel}`;
    }

    return prompt;
  }

  public startNewChat(context?: ConversationContext): void {
    if (!this.model) {
      console.warn('AI model not initialized');
      return;
    }

    if (context) {
      this.conversationContext = context;
    }

    // Start a new chat session with Gemini 1.5 Flash
    this.chatSession = this.model.startChat({
      history: [],
      generationConfig: {
        temperature: 0.7,
        topK: 40,
        topP: 0.95,
        maxOutputTokens: 2048, // Increased for better responses
      },
    });

    // Clear messages and add welcome message
    const welcomeMessage: Message = {
      id: this.generateId(),
      role: 'assistant',
      content: this.getWelcomeMessage(),
      timestamp: new Date()
    };

    this.messagesSubject.next([welcomeMessage]);
  }

  private getWelcomeMessage(): string {
    const context = this.conversationContext;

    if (context.courseName) {
      return `👋 Hello! I'm your AI tutor for **${context.courseName}**. I'm here to help you master this subject!

What would you like to learn today? Feel free to:
- Ask me to explain any concept
- Request examples or practice problems
- Clarify doubts
- Get study tips

You can type or use voice to talk to me. Let's make learning fun! 🎓`;
    }

    return `👋 Hello! I'm your EduBridge AI Tutor. I'm here to help you learn anything you want!

How can I assist you today? You can:
- Ask me to explain topics
- Request examples
- Practice with problems
- Get study guidance

Use text or voice - I'm ready to help! 🚀`;
  }

  public async sendMessage(userMessage: string): Promise<void> {
    if (!this.chatSession || !userMessage.trim()) {
      return;
    }

    // Add user message
    const userMsg: Message = {
      id: this.generateId(),
      role: 'user',
      content: userMessage,
      timestamp: new Date()
    };

    const currentMessages = this.messagesSubject.value;
    this.messagesSubject.next([...currentMessages, userMsg]);

    // Show typing indicator
    this.isTypingSubject.next(true);

    try {
      // Send message to Gemini with context
      const enhancedPrompt = this.enhancePromptWithContext(userMessage);
      const result = await this.chatSession.sendMessage(enhancedPrompt);
      const response = await result.response;
      const text = response.text();

      // Add AI response
      const aiMsg: Message = {
        id: this.generateId(),
        role: 'assistant',
        content: text,
        timestamp: new Date()
      };

      const updatedMessages = this.messagesSubject.value;
      this.messagesSubject.next([...updatedMessages, aiMsg]);

    } catch (error) {
      console.error('Error sending message to AI:', error);

      // Add error message
      const errorMsg: Message = {
        id: this.generateId(),
        role: 'assistant',
        content: "I apologize, but I'm having trouble processing that right now. Could you please try again?",
        timestamp: new Date()
      };

      const updatedMessages = this.messagesSubject.value;
      this.messagesSubject.next([...updatedMessages, errorMsg]);
    } finally {
      this.isTypingSubject.next(false);
    }
  }

  private enhancePromptWithContext(userMessage: string): string {
    const context = this.conversationContext;
    let enhancedPrompt = userMessage;

    // Add context if available
    if (context.courseName || context.moduleName || context.topic) {
      enhancedPrompt = `Context: `;
      if (context.courseName) enhancedPrompt += `Course: ${context.courseName}. `;
      if (context.moduleName) enhancedPrompt += `Module: ${context.moduleName}. `;
      if (context.topic) enhancedPrompt += `Topic: ${context.topic}. `;
      enhancedPrompt += `\n\nStudent Question: ${userMessage}`;
    }

    return enhancedPrompt;
  }

  public setContext(context: ConversationContext): void {
    this.conversationContext = { ...this.conversationContext, ...context };
  }

  public clearContext(): void {
    this.conversationContext = {};
  }

  public getMessages(): Message[] {
    return this.messagesSubject.value;
  }

  public clearMessages(): void {
    this.messagesSubject.next([]);
  }

  public async explainConcept(concept: string, level: string = 'intermediate'): Promise<void> {
    const prompt = `Please explain "${concept}" in a ${level} level. Use simple language, examples, and analogies to make it clear.`;
    await this.sendMessage(prompt);
  }

  public async askForExample(topic: string): Promise<void> {
    const prompt = `Can you provide a practical example of ${topic}? Include code or real-world scenarios if applicable.`;
    await this.sendMessage(prompt);
  }

  public async requestPractice(topic: string): Promise<void> {
    const prompt = `Can you give me a practice problem or exercise about ${topic}? After I answer, please provide feedback.`;
    await this.sendMessage(prompt);
  }

  public async getSummary(topic: string): Promise<void> {
    const prompt = `Please provide a concise summary of the key points about ${topic}.`;
    await this.sendMessage(prompt);
  }

  public async getStudyTips(topic: string): Promise<void> {
    const prompt = `What are the best strategies and tips for learning ${topic} effectively?`;
    await this.sendMessage(prompt);
  }

  private generateId(): string {
    return `msg-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }

  public isAvailable(): boolean {
    return this.isBrowser && !!this.model && !!environment.geminiApiKey;
  }
}
