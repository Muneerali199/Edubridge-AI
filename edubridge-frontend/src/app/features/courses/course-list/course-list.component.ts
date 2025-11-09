import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';
import { CourseService } from '../../../core/services/course.service';
import { VoiceIntegrationService } from '../../../core/services/voice-integration.service';
import { AiTutorService } from '../../../core/services/ai-tutor.service';
import { AiTutorChatComponent } from '../../ai-tutor/ai-tutor-chat/ai-tutor-chat.component';
import { Course, CourseLevel, CourseStatus } from '../../../core/models/course.model';

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
    AiTutorChatComponent
  ],
  templateUrl: './course-list.component.html',
  styleUrls: ['./course-list.component.scss']
})
export class CourseListComponent implements OnInit {
  courses = signal<Course[]>([]);
  loading = signal(false);
  searchQuery = '';
  voiceEnabled = signal(false);
  showAITutor = signal(false);
  private allCourses: Course[] = [];

  constructor(
    private courseService: CourseService,
    private voiceService: VoiceIntegrationService,
    private aiTutorService: AiTutorService
  ) {
    this.voiceEnabled.set(this.voiceService.isSpeechSynthesisSupported());
  }

  ngOnInit(): void {
    // this.loadCourses();
    // Using mock data until backend persistence issue is resolved
    this.courses.set([
      {
        id: '1',
        title: 'Introduction to AI and Machine Learning',
        description: 'Learn the fundamentals of Artificial Intelligence and Machine Learning with hands-on projects and real-world applications.',
        instructorId: 'inst-1',
        instructorName: 'Dr. Sarah Johnson',
        level: CourseLevel.BEGINNER,
        status: CourseStatus.PUBLISHED,
        thumbnailUrl: 'https://via.placeholder.com/400x250/6366f1/ffffff?text=AI+%26+ML',
        price: 99.99,
        discountPrice: 49.99,
        durationHours: 120,
        totalLectures: 45,
        totalEnrollments: 1250,
        averageRating: 4.8,
        totalReviews: 320,
        tags: ['AI', 'Machine Learning', 'Python', 'Data Science'],
        learningOutcomes: ['Understand AI concepts', 'Build ML models', 'Deploy ML applications'],
        requirements: ['Basic Python knowledge'],
        voiceEnabled: true,
        voiceLanguage: 'en-US',
        aiTutorEnabled: true,
        createdAt: '2024-01-15T00:00:00Z',
        updatedAt: '2024-11-01T00:00:00Z'
      },
      {
        id: '2',
        title: 'Full Stack Web Development Bootcamp',
        description: 'Master modern web development with React, Node.js, MongoDB, and deploy production-ready applications.',
        instructorId: 'inst-2',
        instructorName: 'Mark Wilson',
        level: CourseLevel.INTERMEDIATE,
        status: CourseStatus.PUBLISHED,
        thumbnailUrl: 'https://via.placeholder.com/400x250/10b981/ffffff?text=Full+Stack',
        price: 149.99,
        discountPrice: 79.99,
        durationHours: 180,
        totalLectures: 72,
        totalEnrollments: 2100,
        averageRating: 4.9,
        totalReviews: 580,
        tags: ['React', 'Node.js', 'MongoDB', 'Web Development'],
        learningOutcomes: ['Build full-stack apps', 'Deploy to cloud', 'Master modern frameworks'],
        requirements: ['HTML/CSS basics'],
        voiceEnabled: true,
        voiceLanguage: 'en-US',
        aiTutorEnabled: true,
        createdAt: '2024-02-10T00:00:00Z',
        updatedAt: '2024-10-28T00:00:00Z'
      },
      {
        id: '3',
        title: 'Data Science with Python',
        description: 'Dive deep into data analysis, visualization, and statistical modeling using Python, Pandas, and NumPy.',
        instructorId: 'inst-3',
        instructorName: 'Emily Chen',
        level: CourseLevel.BEGINNER,
        status: CourseStatus.PUBLISHED,
        thumbnailUrl: 'https://via.placeholder.com/400x250/f59e0b/ffffff?text=Data+Science',
        price: 89.99,
        discountPrice: 44.99,
        durationHours: 90,
        totalLectures: 38,
        totalEnrollments: 980,
        averageRating: 4.7,
        totalReviews: 210,
        tags: ['Python', 'Data Science', 'Pandas', 'Visualization'],
        learningOutcomes: ['Analyze data', 'Create visualizations', 'Build predictive models'],
        requirements: ['Basic programming'],
        voiceEnabled: true,
        voiceLanguage: 'en-US',
        aiTutorEnabled: false,
        createdAt: '2024-03-20T00:00:00Z',
        updatedAt: '2024-11-05T00:00:00Z'
      },
      {
        id: '4',
        title: 'Advanced Python Programming',
        description: 'Take your Python skills to the next level with advanced concepts including decorators, generators, and more.',
        instructorId: 'inst-3',
        instructorName: 'Emily Chen',
        level: CourseLevel.ADVANCED,
        status: CourseStatus.PUBLISHED,
        thumbnailUrl: 'https://via.placeholder.com/400x250/8b5cf6/ffffff?text=Advanced+Python',
        price: 129.99,
        discountPrice: 69.99,
        durationHours: 60,
        totalLectures: 30,
        totalEnrollments: 650,
        averageRating: 4.6,
        totalReviews: 145,
        tags: ['Python', 'Advanced', 'Programming'],
        learningOutcomes: ['Master advanced Python', 'Write elegant code', 'Implement design patterns'],
        requirements: ['Intermediate Python'],
        voiceEnabled: true,
        voiceLanguage: 'en-US',
        aiTutorEnabled: true,
        createdAt: '2024-03-01T00:00:00Z',
        updatedAt: '2024-11-03T00:00:00Z'
      },
      {
        id: '5',
        title: 'Mobile App Development with React Native',
        description: 'Build cross-platform mobile applications for iOS and Android using React Native.',
        instructorId: 'inst-2',
        instructorName: 'Mark Wilson',
        level: CourseLevel.INTERMEDIATE,
        status: CourseStatus.PUBLISHED,
        thumbnailUrl: 'https://via.placeholder.com/400x250/ec4899/ffffff?text=React+Native',
        price: 119.99,
        discountPrice: 59.99,
        durationHours: 100,
        totalLectures: 50,
        totalEnrollments: 1420,
        averageRating: 4.8,
        totalReviews: 267,
        tags: ['React Native', 'Mobile', 'iOS', 'Android'],
        learningOutcomes: ['Build mobile apps', 'Publish to app stores', 'Master mobile UX'],
        requirements: ['JavaScript and React'],
        voiceEnabled: true,
        voiceLanguage: 'en-US',
        aiTutorEnabled: true,
        createdAt: '2024-04-01T00:00:00Z',
        updatedAt: '2024-11-07T00:00:00Z'
      }
    ]);

    // Store all courses for filtering
    this.allCourses = this.courses();
  }

  loadCourses(): void {
    this.loading.set(true);
    this.courseService.getCourses(0, 12).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.courses.set(response.data.content || []);
        }
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading courses:', error);
        this.loading.set(false);
      }
    });
  }

  searchCourses(): void {
    if (!this.searchQuery.trim()) {
      // Show all courses when search is cleared (using mock data)
      this.ngOnInit();
      return;
    }

    // Filter mock data locally
    const query = this.searchQuery.toLowerCase();
    const allCourses = this.courses();
    const filtered = allCourses.filter(course =>
      course.title.toLowerCase().includes(query) ||
      course.description.toLowerCase().includes(query) ||
      course.instructorName.toLowerCase().includes(query) ||
      course.tags.some(tag => tag.toLowerCase().includes(query))
    );
    this.courses.set(filtered);
  }

  async speakCourseDescription(course: Course): Promise<void> {
    try {
      await this.voiceService.speak({
        text: `${course.title}. ${course.description}`,
        lang: course.voiceLanguage || 'en-US'
      });
    } catch (error) {
      console.error('Error speaking course description:', error);
    }
  }

  stopSpeaking(): void {
    this.voiceService.stopSpeaking();
  }

  getLevelColor(level: string): string {
    const colors: { [key: string]: string } = {
      BEGINNER: 'primary',
      INTERMEDIATE: 'accent',
      ADVANCED: 'warn',
      EXPERT: ''
    };
    return colors[level] || 'primary';
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  }

  formatNumber(num: number): string {
    if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'k';
    }
    return num.toString();
  }

  getCourseIcon(category: string): string {
    const icons: { [key: string]: string } = {
      'Programming': 'code',
      'Design': 'palette',
      'Business': 'business_center',
      'Marketing': 'campaign',
      'Data Science': 'analytics',
      'AI/ML': 'psychology',
      'Web Development': 'web',
      'Mobile': 'smartphone',
      'default': 'school'
    };
    return icons[category] || icons['default'];
  }

  getLevelIcon(level: string): string {
    const icons: { [key: string]: string } = {
      'BEGINNER': 'school',
      'INTERMEDIATE': 'trending_up',
      'ADVANCED': 'emoji_events',
      'EXPERT': 'workspace_premium'
    };
    return icons[level] || 'school';
  }

  getDiscountPercent(original: number, discounted: number): number {
    return Math.round(((original - discounted) / original) * 100);
  }

  filterByLevel(level: string): void {
    if (level === 'ALL') {
      this.courses.set(this.allCourses);
    } else {
      const filtered = this.allCourses.filter(c => c.level === level);
      this.courses.set(filtered);
    }
  }

  toggleAITutor(): void {
    this.showAITutor.set(!this.showAITutor());

    if (this.showAITutor()) {
      // Set context for AI tutor
      this.aiTutorService.setContext({
        topic: 'Course Selection',
        userLevel: 'browsing'
      });
    }
  }

  askAIAboutCourse(course: Course): void {
    // Open AI tutor and ask about the course
    this.showAITutor.set(true);

    // Set course context
    this.aiTutorService.setContext({
      courseId: course.id,
      courseName: course.title,
      topic: course.title
    });

    // Send initial message about the course
    setTimeout(() => {
      const question = `Tell me more about the "${course.title}" course. What will I learn, and is it suitable for me?`;
      this.aiTutorService.sendMessage(question);
    }, 500);
  }
}
