import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { CourseService } from '../../../core/services/course.service';
import { Instructor } from '../../../core/models/course.model';

@Component({
  selector: 'app-instructor-list',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule
  ],
  template: `
    <div class="instructors-container">
      <h2>Top Instructors</h2>
      <div class="instructors-grid">
        @for (instructor of instructors(); track instructor.id) {
          <mat-card class="instructor-card">
            <div class="profile-section">
              <div class="profile-image">
                @if (instructor.profileImageUrl) {
                  <img [src]="instructor.profileImageUrl" [alt]="instructor.name">
                } @else {
                  <mat-icon>person</mat-icon>
                }
              </div>
              @if (instructor.isVerified) {
                <mat-icon class="verified-badge" matTooltip="Verified Instructor">verified</mat-icon>
              }
            </div>

            <mat-card-header>
              <mat-card-title>{{ instructor.name }}</mat-card-title>
              <mat-card-subtitle>{{ instructor.headline }}</mat-card-subtitle>
            </mat-card-header>

            <mat-card-content>
              <div class="stats">
                <div class="stat">
                  <strong>{{ instructor.totalStudents }}</strong>
                  <span>Students</span>
                </div>
                <div class="stat">
                  <strong>{{ instructor.totalCourses }}</strong>
                  <span>Courses</span>
                </div>
                <div class="stat">
                  <strong>{{ instructor.averageRating }}</strong>
                  <span>Rating</span>
                </div>
              </div>

              <div class="expertise">
                <mat-chip-set>
                  @for (skill of instructor.expertise.slice(0, 3); track skill) {
                    <mat-chip>{{ skill }}</mat-chip>
                  }
                </mat-chip-set>
              </div>

              @if (instructor.voiceTutorEnabled) {
                <div class="voice-tutor-badge">
                  <mat-icon>record_voice_over</mat-icon>
                  <span>AI Voice Tutor Available</span>
                </div>
              }
            </mat-card-content>

            <mat-card-actions>
              <button mat-button color="primary">View Profile</button>
              <button mat-button>View Courses</button>
            </mat-card-actions>
          </mat-card>
        }
      </div>
    </div>
  `,
  styles: [`
    .instructors-container {
      padding: 2rem;
      max-width: 1400px;
      margin: 0 auto;

      h2 {
        text-align: center;
        margin-bottom: 2rem;
        font-size: 2rem;
      }
    }

    .instructors-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 2rem;
    }

    .instructor-card {
      text-align: center;
      transition: transform 0.3s ease;

      &:hover {
        transform: translateY(-5px);
      }

      .profile-section {
        position: relative;
        margin: 1rem 0;

        .profile-image {
          width: 120px;
          height: 120px;
          border-radius: 50%;
          margin: 0 auto;
          overflow: hidden;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          display: flex;
          align-items: center;
          justify-content: center;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          mat-icon {
            font-size: 60px;
            width: 60px;
            height: 60px;
            color: white;
          }
        }

        .verified-badge {
          position: absolute;
          bottom: 0;
          right: calc(50% - 70px);
          background: #4caf50;
          color: white;
          border-radius: 50%;
          padding: 4px;
        }
      }

      .stats {
        display: flex;
        justify-content: space-around;
        margin: 1rem 0;
        padding: 1rem 0;
        border-top: 1px solid #eee;
        border-bottom: 1px solid #eee;

        .stat {
          display: flex;
          flex-direction: column;

          strong {
            font-size: 1.2rem;
            color: #667eea;
          }

          span {
            font-size: 0.75rem;
            color: #666;
          }
        }
      }

      .expertise {
        margin: 1rem 0;
      }

      .voice-tutor-badge {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        background: #e3f2fd;
        color: #1976d2;
        padding: 0.5rem;
        border-radius: 8px;
        margin-top: 1rem;
        font-size: 0.875rem;

        mat-icon {
          font-size: 20px;
          width: 20px;
          height: 20px;
        }
      }
    }
  `]
})
export class InstructorListComponent implements OnInit {
  instructors = signal<Instructor[]>([]);

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    // this.loadInstructors();
    // Using mock data until backend is available
    this.instructors.set([
      {
        id: 'inst-1',
        userId: 'user-1',
        name: 'Dr. Sarah Johnson',
        email: 'sarah.johnson@edubridge.com',
        biography: 'AI and Machine Learning expert with 10+ years of experience in academic research and industry applications. Former lead researcher at top tech companies.',
        expertise: ['Artificial Intelligence', 'Machine Learning', 'Deep Learning', 'Neural Networks'],
        profileImageUrl: 'https://ui-avatars.com/api/?name=Sarah+Johnson&background=6366f1&color=fff&size=200',
        totalStudents: 5000,
        totalCourses: 8,
        averageRating: 4.8,
        totalReviews: 1240,
        isVerified: true,
        isTopInstructor: true,
        voiceTutorEnabled: true,
        aiAssistantEnabled: true,
        headline: 'AI & ML Expert | Former Tech Lead',
        linkedinUrl: 'https://linkedin.com/in/sarahjohnson',
        twitterUrl: 'https://twitter.com/sarahjohnson',
        createdAt: '2023-01-15T00:00:00Z',
        updatedAt: '2024-11-01T00:00:00Z'
      },
      {
        id: 'inst-2',
        userId: 'user-2',
        name: 'Mark Wilson',
        email: 'mark.wilson@edubridge.com',
        biography: 'Full-stack developer and instructor passionate about modern web technologies. Over 15 years building scalable applications.',
        expertise: ['Web Development', 'React', 'Node.js', 'MongoDB', 'TypeScript'],
        profileImageUrl: 'https://ui-avatars.com/api/?name=Mark+Wilson&background=10b981&color=fff&size=200',
        totalStudents: 8000,
        totalCourses: 12,
        averageRating: 4.9,
        totalReviews: 2150,
        isVerified: true,
        isTopInstructor: true,
        voiceTutorEnabled: true,
        aiAssistantEnabled: true,
        headline: 'Full Stack Developer | Web Technologies Expert',
        linkedinUrl: 'https://linkedin.com/in/markwilson',
        websiteUrl: 'https://markwilson.dev',
        createdAt: '2022-08-20T00:00:00Z',
        updatedAt: '2024-10-28T00:00:00Z'
      },
      {
        id: 'inst-3',
        userId: 'user-3',
        name: 'Emily Chen',
        email: 'emily.chen@edubridge.com',
        biography: 'Data scientist helping students master data analysis and visualization. PhD in Statistics with industry experience in data-driven decision making.',
        expertise: ['Data Science', 'Python', 'Statistics', 'Data Visualization', 'Machine Learning'],
        profileImageUrl: 'https://ui-avatars.com/api/?name=Emily+Chen&background=f59e0b&color=fff&size=200',
        totalStudents: 3500,
        totalCourses: 6,
        averageRating: 4.7,
        totalReviews: 890,
        isVerified: true,
        isTopInstructor: true,
        voiceTutorEnabled: false,
        aiAssistantEnabled: false,
        headline: 'Data Scientist | Statistics PhD',
        linkedinUrl: 'https://linkedin.com/in/emilychen',
        twitterUrl: 'https://twitter.com/emilychen',
        createdAt: '2023-03-10T00:00:00Z',
        updatedAt: '2024-11-05T00:00:00Z'
      },
      {
        id: 'inst-4',
        userId: 'user-4',
        name: 'David Rodriguez',
        email: 'david.rodriguez@edubridge.com',
        biography: 'Cloud architect and DevOps expert specializing in AWS and Azure. Helping teams build scalable cloud infrastructure.',
        expertise: ['Cloud Architecture', 'AWS', 'Azure', 'DevOps', 'Kubernetes'],
        profileImageUrl: 'https://ui-avatars.com/api/?name=David+Rodriguez&background=8b5cf6&color=fff&size=200',
        totalStudents: 4200,
        totalCourses: 9,
        averageRating: 4.8,
        totalReviews: 1050,
        isVerified: true,
        isTopInstructor: true,
        voiceTutorEnabled: true,
        aiAssistantEnabled: true,
        headline: 'Cloud Architect | AWS & Azure Expert',
        linkedinUrl: 'https://linkedin.com/in/davidrodriguez',
        createdAt: '2022-11-05T00:00:00Z',
        updatedAt: '2024-11-02T00:00:00Z'
      }
    ]);
  }

  loadInstructors(): void {
    this.courseService.getTopInstructors(12).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.instructors.set(response.data);
        }
      },
      error: (error) => {
        console.error('Error loading instructors:', error);
      }
    });
  }
}
