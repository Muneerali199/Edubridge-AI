import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'auth/register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'courses',
    loadComponent: () => import('./features/courses/course-list/course-list.component').then(m => m.CourseListComponent)
  },
  {
    path: 'instructors',
    loadComponent: () => import('./features/courses/instructor-list/instructor-list.component').then(m => m.InstructorListComponent)
  },
  {
    path: '**',
    redirectTo: '/'
  }
];

