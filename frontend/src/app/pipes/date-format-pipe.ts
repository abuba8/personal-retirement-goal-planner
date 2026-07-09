import { DatePipe } from "@angular/common";
import { Pipe, PipeTransform } from "@angular/core";

@Pipe({ name: "dateFormat" })
export class DateFormatPipe implements PipeTransform {
    private datePipe = new DatePipe("en-US");

    transform(value: string | Date | null | undefined): string | null {
        if(!value) {
            return null;
        }
        return this.datePipe.transform(value, "MMM d, y");
    }
}