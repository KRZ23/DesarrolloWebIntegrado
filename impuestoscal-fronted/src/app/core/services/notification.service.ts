import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export interface AppNotification {
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private queue = new Subject<AppNotification>();

  get stream(): Observable<AppNotification> { return this.queue.asObservable(); }

  push(n: AppNotification) { this.queue.next(n); }
  success(message: string) { this.push({ type: 'success', message }); }
  error(message: string) { this.push({ type: 'error', message }); }
  info(message: string) { this.push({ type: 'info', message }); }
  warning(message: string) { this.push({ type: 'warning', message }); }
}
