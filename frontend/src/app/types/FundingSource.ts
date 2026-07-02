import { SourceType } from "./enums/SourceType";

export type FundingSource = {
    id: bigint;
    name: string;
    institution: string;
    notes: string;
    sourceType: SourceType;
}