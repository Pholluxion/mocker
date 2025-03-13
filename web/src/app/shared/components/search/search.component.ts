import { Component, EventEmitter, Input, Output } from '@angular/core'; // Importa los decoradores y clases necesarias de Angular para definir componentes y gestionar eventos.

@Component({
  selector: 'app-shared-search',
  imports: [], // No se importan módulos adicionales en este componente.
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss', 
})
export class SearchComponent {
  @Input() // Permite recibir un valor desde el componente padre.
  title: string = 'Busqueda de elementos'; // Define un título predeterminado para el componente.

  @Output() // Marca la propiedad para emitir eventos al componente padre.
  public newSearch: EventEmitter<string> = new EventEmitter(); // Crea una instancia de EventEmitter para emitir cadenas de texto con la búsqueda.

  // Método que se ejecuta cuando se envía la entrada de búsqueda.
  // Recibe un elemento input y emite su valor a través del EventEmitter.
  sendSearchInput(input: HTMLInputElement) {
    this.newSearch.emit(input.value); // Emite el valor actual del input al componente padre.
  }
}
