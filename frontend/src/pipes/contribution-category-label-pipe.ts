import { Pipe, PipeTransform } from "@angular/core";
import { ContributionCategory, ContributionCategoryLabel } from "../app/types/enums/ContributionCategory";


@Pipe({ name: 'contributionCategoryLabel' })
export class ContributionCategoryLabelPipe implements PipeTransform {
    transform(value: ContributionCategory): string {
        return ContributionCategoryLabel[value] ?? value;
    }
}