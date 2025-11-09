export interface Course {
  id: string;
  title: string;
  subtitle?: string;
  description: string;
  instructorId: string;
  instructorName: string;
  level: CourseLevel;
  status: CourseStatus;
  thumbnailUrl?: string;
  previewVideoUrl?: string;
  price?: number;
  discountPrice?: number;
  durationHours?: number;
  totalLectures: number;
  totalEnrollments: number;
  averageRating: number;
  totalReviews: number;
  tags: string[];
  learningOutcomes: string[];
  requirements: string[];
  voiceEnabled: boolean;
  voiceLanguage: string;
  aiTutorEnabled: boolean;
  publishedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Instructor {
  id: string;
  userId: string;
  name: string;
  email: string;
  profileImageUrl?: string;
  headline?: string;
  biography?: string;
  websiteUrl?: string;
  linkedinUrl?: string;
  twitterUrl?: string;
  youtubeUrl?: string;
  expertise: string[];
  totalStudents: number;
  totalCourses: number;
  totalReviews: number;
  averageRating: number;
  isVerified: boolean;
  isTopInstructor: boolean;
  voiceTutorEnabled: boolean;
  voiceName?: string;
  aiAssistantEnabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum CourseLevel {
  BEGINNER = 'BEGINNER',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED',
  EXPERT = 'EXPERT'
}

export enum CourseStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  ARCHIVED = 'ARCHIVED',
  UNDER_REVIEW = 'UNDER_REVIEW'
}

export interface VoiceRequest {
  text: string;
  languageCode?: string;
  voiceName?: string;
}

export interface VoiceResponse {
  audioUrl: string;
  text: string;
}

export interface VoiceOption {
  name: string;
  gender: string;
  type: string;
}
