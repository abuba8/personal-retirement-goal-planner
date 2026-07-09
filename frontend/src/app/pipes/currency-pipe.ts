import { CurrencyPipe } from "@angular/common";
import { Pipe, PipeTransform } from "@angular/core";

@Pipe({ name: "currency"})
export class currencyPipe implements PipeTransform {
    private currencyPipe = new CurrencyPipe("en-US");

    transform(value: number | string | null | undefined): string | null {
        if (value === null || value === undefined || value === "") {
            return null
        }
        return this.currencyPipe.transform(value, "USD", "symbol", "1.2-2");
    }
}