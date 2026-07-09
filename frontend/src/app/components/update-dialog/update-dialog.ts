import { Component, model, output, signal } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { Contribution } from '../../types/Contribution';

@Component({
  selector: 'app-update-dialog',
  imports: [ButtonModule, DialogModule],
  templateUrl: './update-dialog.html',
  styleUrl: './update-dialog.css',
})
export class UpdateDialog {
  visible = model<boolean>(false);
  edit = output<void>();
  delete = output<void>();
}
