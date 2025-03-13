import { CommonModule } from '@angular/common'; // Importa el módulo CommonModule, que proporciona directivas y funcionalidades comunes de Angular.
import { Component, Input } from '@angular/core'; // Importa los decoradores Component e Input desde Angular, necesarios para definir componentes y recibir datos.

@Component({
  selector: 'app-log-console',
  imports: [CommonModule], // Importa el módulo CommonModule para que el componente pueda usar sus directivas.
  templateUrl: './console.component.html',
  styleUrl: './console.component.scss', 
})
export class ConsoleComponent { // Declara la clase ConsoleComponent que contiene la lógica y estructura del componente.
  @Input() // Decorador que marca la propiedad para recibir datos desde un componente padre.
  public dataLogs: any[] = []; // Define una propiedad pública dataLogs, que es un arreglo de cualquier tipo, inicializado como vacío.
}
