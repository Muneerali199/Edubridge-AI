import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatCardModule
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  features = [
    {
      icon: 'psychology',
      title: 'AI-Powered Tutor',
      description: 'Get personalized learning assistance from our advanced AI tutor powered by Google Gemini'
    },
    {
      icon: 'record_voice_over',
      title: 'Voice Integration',
      description: 'Learn naturally with voice input and output - ask questions and hear explanations'
    },
    {
      icon: 'school',
      title: 'Expert Instructors',
      description: 'Learn from industry experts with years of real-world experience'
    },
    {
      icon: 'devices',
      title: 'Learn Anywhere',
      description: 'Access courses on any device - desktop, tablet, or mobile'
    },
    {
      icon: 'emoji_events',
      title: 'Certificates',
      description: 'Earn recognized certificates upon course completion'
    },
    {
      icon: 'trending_up',
      title: 'Track Progress',
      description: 'Monitor your learning journey with detailed progress tracking'
    }
  ];

  stats = [
    { value: '500+', label: 'Courses' },
    { value: '50K+', label: 'Students' },
    { value: '100+', label: 'Instructors' },
    { value: '4.8/5', label: 'Rating' }
  ];
}
