import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../dashboard.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home implements OnInit {

  total: number = 0;
  pendientes: number = 0;
  vencidos: number = 0;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.cargarResumen();
  }

  cargarResumen(): void {
    this.dashboardService.getResumen().subscribe({
      next: (data) => {
        this.total = data.total;
        this.pendientes = data.pendientes;
        this.vencidos = data.vencidos;
      },
      error: (err) => {
        console.error('Error cargando dashboard:', err);
      }
    });
  }
}

