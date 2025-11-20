import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenService {
  private storageKey = 'token';

  get(): string | null {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem(this.storageKey);
  }

  set(token: string): void {
    if (typeof window === 'undefined') return;
    localStorage.setItem(this.storageKey, token);
  }

  clear(): void {
    if (typeof window === 'undefined') return;
    localStorage.removeItem(this.storageKey);
  }

  // Placeholder para decodificar y validar expiración futura
  isExpired(): boolean {
    const t = this.get();
    if (!t) return true;
    // Si el token fuera JWT real se podría decodificar aquí.
    try {
      const payload = this.decodePayload();
      if (!payload || !payload.exp) return false;
      const now = Math.floor(Date.now() / 1000);
      return payload.exp < now;
    } catch {
      return false;
    }
  }

  decodePayload(): any | null {
    const token = this.get();
    if (!token) return null;
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    try {
      const decoded = atob(parts[1].replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded);
    } catch {
      return null;
    }
  }

  getClaim<T = any>(claim: string): T | null {
    const payload = this.decodePayload();
    return payload && claim in payload ? payload[claim] as T : null;
  }

  getRoles(): string[] {
    const roles = this.getClaim<string[]>('roles');
    return Array.isArray(roles) ? roles : [];
  }
}
