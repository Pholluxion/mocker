import { CommonModule } from '@angular/common'; // Importa el módulo CommonModule, que proporciona directivas y funcionalidades comunes de Angular.
import {
  Component,
  ElementRef,
  HostListener,
  Input,
  ViewChild,
} from '@angular/core'; // Importa los decoradores Component e Input desde Angular, necesarios para definir componentes y recibir datos.

@Component({
  selector: 'app-log-console',
  imports: [CommonModule], // Importa el módulo CommonModule para que el componente pueda usar sus directivas.
  templateUrl: './console.component.html',
  styleUrl: './console.component.scss',
})
export class ConsoleComponent {
  // Declara la clase ConsoleComponent que contiene la lógica y estructura del componente.
  @Input() // Decorador que marca la propiedad para recibir datos desde un componente padre.
  public dataLogs: any[] = []; // Define una propiedad pública dataLogs, que es un arreglo de cualquier tipo, inicializado como vacío.

  @ViewChild('logContainer') private logContainer!: ElementRef;

  private userScrolling = false; // Flag para detectar si el usuario está desplazándose manualmente

  ngAfterViewChecked() {
    if (!this.userScrolling) {
      // Si el usuario no está navegando, hacer scroll automático
      this.scrollToBottom();
    }
  }

  private scrollToBottom(): void {
    if (this.logContainer) {
      this.logContainer.nativeElement.scrollTop =
        this.logContainer.nativeElement.scrollHeight;
    }
  }

  // Detecta si el usuario está navegando manualmente y pausa el auto-scroll
  @HostListener('scroll', ['$event'])
  onScroll(event: Event) {
    const element = event.target as HTMLElement;
    const isAtBottom =
      element.scrollHeight - element.scrollTop <= element.clientHeight + 5;

    this.userScrolling = !isAtBottom; // Solo reactiva el scroll si el usuario está abajo
  }
}
