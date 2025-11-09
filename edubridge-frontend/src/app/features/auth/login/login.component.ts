import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatIconModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = signal(false);
  hidePassword = signal(true);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    const loginRequest: LoginRequest = this.loginForm.value;

    this.authService.login(loginRequest).subscribe({
      next: (response) => {
        this.loading.set(false);
        if (response.success) {
          this.snackBar.open('Login successful!', 'Close', {
            duration: 3000,
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
          this.router.navigate(['/dashboard']);
        } else {
          this.snackBar.open(response.message || 'Login failed', 'Close', {
            duration: 5000,
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        }
      },
      error: (error) => {
        this.loading.set(false);
        const errorMessage = error.error?.message || 'Login failed. Please try again.';
        this.snackBar.open(errorMessage, 'Close', {
          duration: 5000,
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }

  togglePasswordVisibility(): void {
    this.hidePassword.set(!this.hidePassword());
  }

  getEmailError(): string {
    const emailControl = this.loginForm.get('email');
    if (emailControl?.hasError('required')) {
      return 'Email is required';
    }
    if (emailControl?.hasError('email')) {
      return 'Please enter a valid email';
    }
    return '';
  }

  getPasswordError(): string {
    const passwordControl = this.loginForm.get('password');
    if (passwordControl?.hasError('required')) {
      return 'Password is required';
    }
    if (passwordControl?.hasError('minlength')) {
      return 'Password must be at least 6 characters';
    }
    return '';
  }
}
