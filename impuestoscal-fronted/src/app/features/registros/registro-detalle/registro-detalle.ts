import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { RegistrosService, Registro } from '../services/registros.service';

@Component({
  selector: 'app-registro-detalle',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './registro-detalle.html'
})
export class RegistroDetalleComponent implements OnInit {
  registro: Registro | null = null;
  loading = false;

  constructor(private route: ActivatedRoute, private svc: RegistrosService, private router: Router) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.loading = true;
      this.svc.obtener(Number(idParam)).subscribe({
        next: r => { this.registro = r; this.loading = false; },
        error: e => { console.error(e); this.loading = false; }
      });
    }
  }

  volver() { this.router.navigate(['/registros']); }
  editar() { if (this.registro?.id) this.router.navigate(['/registros', this.registro.id, 'editar']); }
}
