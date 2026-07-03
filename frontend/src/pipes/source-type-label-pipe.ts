import { Pipe, PipeTransform } from "@angular/core";
import { SourceType, SourceTypeLabels } from "../app/types/enums/SourceType";


@Pipe({ name: 'sourceTypeLabel' })
export class SourceTypeLabelPipe implements PipeTransform {
    transform(value: SourceType): string {
        return SourceTypeLabels[value] ?? value;
    }
}