import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from '../services/auth.service';
import {jwtDecode} from 'jwt-decode';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);

  const token = localStorage.getItem('econexo_token');

  if (token) {
    try {
      const decoded: any = jwtDecode(token);
      const isExpired = decoded.exp * 1000 < Date.now();
      if (!isExpired) return true;
    } catch {
    }
  }

  router.navigate(['/login']);

  return false;
};

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const rawRole = authService.getUserRole();
    const userRole = rawRole?.replace('ROLE_', '') ?? null;

    if (userRole && allowedRoles.includes(userRole)) {
      return true;
    }

    router.navigate(['/login']);
    return false;
  };
};
