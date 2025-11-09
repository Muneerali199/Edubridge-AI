import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProfileService } from '../../core/services/profile.service';
import { UserProfile } from '../../core/models/profile.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  profileForm!: FormGroup;
  profile: UserProfile | null = null;
  loading = false;
  editing = false;

  constructor(
    private fb: FormBuilder,
    private profileService: ProfileService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadProfile();
  }

  initializeForm(): void {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      phone: ['', [Validators.minLength(10), Validators.maxLength(20)]]
    });
  }

  loadProfile(): void {
    this.loading = true;
    this.profileService.getProfile().subscribe({
      next: (response) => {
        this.profile = response.data;
        this.profileForm.patchValue({
          firstName: response.data.firstName,
          lastName: response.data.lastName,
          phone: response.data.phone || ''
        });
        this.profileForm.disable();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading profile:', error);
        this.snackBar.open('Failed to load profile', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  toggleEdit(): void {
    if (this.editing) {
      // Cancel editing - reset form
      this.profileForm.patchValue({
        firstName: this.profile?.firstName || '',
        lastName: this.profile?.lastName || '',
        phone: this.profile?.phone || ''
      });
      this.profileForm.disable();
    } else {
      this.profileForm.enable();
    }
    this.editing = !this.editing;
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.snackBar.open('Please fill in all required fields correctly', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;
    this.profileService.updateProfile(this.profileForm.value).subscribe({
      next: (response) => {
        this.profile = response.data;
        this.profileForm.disable();
        this.editing = false;
        this.loading = false;
        this.snackBar.open('Profile updated successfully!', 'Close', { duration: 3000 });
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        this.snackBar.open(
          error.error?.message || 'Failed to update profile',
          'Close',
          { duration: 3000 }
        );
        this.loading = false;
      }
    });
  }

  getErrorMessage(field: string): string {
    const control = this.profileForm.get(field);
    if (control?.hasError('required')) {
      return `${field.charAt(0).toUpperCase() + field.slice(1)} is required`;
    }
    if (control?.hasError('minlength')) {
      return `${field.charAt(0).toUpperCase() + field.slice(1)} is too short`;
    }
    if (control?.hasError('maxlength')) {
      return `${field.charAt(0).toUpperCase() + field.slice(1)} is too long`;
    }
    return '';
  }
}
