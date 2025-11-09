import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  isLoggedIn = signal(false);

  constructor(private router: Router) {
    // Check if user is logged in
    const token = localStorage.getItem('auth_token');
    this.isLoggedIn.set(!!token);
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    this.isLoggedIn.set(false);
    this.router.navigate(['/']);
  }
}
