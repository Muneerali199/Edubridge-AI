import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Subject, Observable } from 'rxjs';

export interface VoiceSynthesisOptions {
  text: string;
  lang?: string;
  voice?: SpeechSynthesisVoice;
  rate?: number;
  pitch?: number;
  volume?: number;
}

@Injectable({
  providedIn: 'root'
})
export class VoiceIntegrationService {
  private synthesis: SpeechSynthesis | null = null;
  private recognition: any; // SpeechRecognition
  private recognitionSubject = new Subject<string>();
  private isListening = false;
  private isBrowser: boolean;

  constructor(@Inject(PLATFORM_ID) platformId: Object) {
    this.isBrowser = isPlatformBrowser(platformId);

    // Only initialize browser APIs if running in browser
    if (this.isBrowser) {
      this.synthesis = window.speechSynthesis;

      // Initialize Speech Recognition if available
      if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
        const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
        this.recognition = new SpeechRecognition();
        this.recognition.continuous = false;
        this.recognition.interimResults = false;
        this.recognition.lang = 'en-US';

        this.recognition.onresult = (event: any) => {
          const transcript = event.results[0][0].transcript;
          this.recognitionSubject.next(transcript);
        };

        this.recognition.onerror = (event: any) => {
          console.error('Speech recognition error:', event.error);
          this.recognitionSubject.error(event.error);
        };

        this.recognition.onend = () => {
          this.isListening = false;
        };
      }
    }
  }

  /**
   * Convert text to speech using Web Speech API
   */
  speak(options: VoiceSynthesisOptions): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!this.synthesis) {
        reject(new Error('Speech synthesis not supported'));
        return;
      }

      // Cancel any ongoing speech
      this.synthesis.cancel();

      const utterance = new SpeechSynthesisUtterance(options.text);
      utterance.lang = options.lang || 'en-US';
      utterance.rate = options.rate || 1.0;
      utterance.pitch = options.pitch || 1.0;
      utterance.volume = options.volume || 1.0;

      if (options.voice) {
        utterance.voice = options.voice;
      }

      utterance.onend = () => resolve();
      utterance.onerror = (event) => reject(event);

      this.synthesis.speak(utterance);
    });
  }

  /**
   * Stop current speech
   */
  stopSpeaking(): void {
    if (this.synthesis) {
      this.synthesis.cancel();
    }
  }

  /**
   * Pause current speech
   */
  pauseSpeaking(): void {
    if (this.synthesis) {
      this.synthesis.pause();
    }
  }

  /**
   * Resume paused speech
   */
  resumeSpeaking(): void {
    if (this.synthesis) {
      this.synthesis.resume();
    }
  }

  /**
   * Get available voices
   */
  getVoices(): SpeechSynthesisVoice[] {
    return this.synthesis ? this.synthesis.getVoices() : [];
  }

  /**
   * Start listening for speech input
   */
  startListening(lang: string = 'en-US'): Observable<string> {
    if (!this.recognition) {
      throw new Error('Speech recognition not supported');
    }

    this.recognition.lang = lang;
    this.recognition.start();
    this.isListening = true;

    return this.recognitionSubject.asObservable();
  }

  /**
   * Stop listening for speech input
   */
  stopListening(): void {
    if (this.recognition && this.isListening) {
      this.recognition.stop();
      this.isListening = false;
    }
  }

  /**
   * Check if currently listening
   */
  getIsListening(): boolean {
    return this.isListening;
  }

  /**
   * Check if speech synthesis is supported
   */
  isSpeechSynthesisSupported(): boolean {
    return this.isBrowser && 'speechSynthesis' in window;
  }

  /**
   * Check if speech recognition is supported
   */
  isSpeechRecognitionSupported(): boolean {
    return this.isBrowser && ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window);
  }

  /**
   * Read course content with voice
   */
  async readCourseContent(content: string, lang: string = 'en-US'): Promise<void> {
    // Split long content into chunks
    const chunks = this.splitIntoChunks(content, 200);

    for (const chunk of chunks) {
      await this.speak({ text: chunk, lang });
    }
  }

  /**
   * Split text into manageable chunks
   */
  private splitIntoChunks(text: string, maxLength: number): string[] {
    const sentences = text.match(/[^.!?]+[.!?]+/g) || [text];
    const chunks: string[] = [];
    let currentChunk = '';

    for (const sentence of sentences) {
      if ((currentChunk + sentence).length > maxLength && currentChunk) {
        chunks.push(currentChunk.trim());
        currentChunk = sentence;
      } else {
        currentChunk += ' ' + sentence;
      }
    }

    if (currentChunk) {
      chunks.push(currentChunk.trim());
    }

    return chunks;
  }
}
