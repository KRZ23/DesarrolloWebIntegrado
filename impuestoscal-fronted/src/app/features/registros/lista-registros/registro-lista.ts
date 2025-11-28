import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RegistrosService, Registro } from '../services/registros.service';
import { TokenService } from '../../../core/services/token.service';

@Component({
  selector: 'app-registro-lista',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './registro-lista.html',
  styleUrls: ['./registro-lista.css']
})
export class RegistroListaComponent implements OnInit {
  registros: Registro[] = [];
  filtroTipo = '';
  filtroEstado = '';
  loading = false;
  isAdmin = false;

  constructor(private svc: RegistrosService, private router: Router, private tokenSvc: TokenService) {}

  ngOnInit(): void {
    this.isAdmin = this.tokenSvc.getRoles().includes('ADMIN');
    this.cargar();
  }

  cargar() {
    this.loading = true;
    this.svc.listar().subscribe({
      next: r => { this.registros = r; this.loading = false; },
      error: e => { console.error(e); this.loading = false; }
    });
  }

  get registrosFiltrados(): Registro[] {
    return this.registros.filter(r =>
      (!this.filtroTipo || r.tipoImpuesto.toLowerCase().includes(this.filtroTipo.toLowerCase())) &&
      (!this.filtroEstado || r.estado === this.filtroEstado)
    );
  }

  get countPendiente(): number { return this.registrosFiltrados.filter(r => r.estado === 'PENDIENTE').length; }
  get countPagado(): number { return this.registrosFiltrados.filter(r => r.estado === 'PAGADO').length; }
  get countVencido(): number { return this.registrosFiltrados.filter(r => r.estado === 'VENCIDO').length; }

  irNuevo() { this.router.navigate(['/registros/nuevo']); }
  verDetalle(id: number) { this.router.navigate(['/registros', id]); }
  editar(id: number) { this.router.navigate(['/registros', id, 'editar']); }
  eliminar(id: number) {
    if (!confirm('Eliminar registro?')) return;
    this.svc.eliminar(id).subscribe({ next: () => this.cargar(), error: e => console.error(e) });
  }

  verTodosAdmin() {
    if (!this.isAdmin) return;
    this.loading = true;
    this.svc.listarAdminTodos().subscribe({
      next: r => { this.registros = r; this.loading = false; },
      error: e => { console.error(e); this.loading = false; }
    });
  }
}
