import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class TokenService {

    constructor() { }

    public setToken(token: string): void {
        if (typeof window !== 'undefined') {
            window.localStorage.setItem('token', token);
        }
    }

    public getToken(): string | null {
        if (typeof window !== 'undefined') {
            return window.localStorage.getItem('token');
        }
        return null;
    }

    public setRoles(roles: string[]): void {
        if (typeof window !== 'undefined') {
            window.localStorage.setItem('roles', JSON.stringify(roles));
        }
    }

    public getRoles(): string[] {
        if (typeof window !== 'undefined') {
            const roles = window.localStorage.getItem('roles');
            if (roles) {
                return JSON.parse(roles);
            }
        }
        return [];
    }

    public logOut(): void {
        if (typeof window !== 'undefined') {
            window.localStorage.clear();
        }
    }
}
