import { Contribution } from "../Contribution"

export interface ContributionPage {
    content: Contribution[]
    totalElements: number
    totalPages: number
    size: number
    number: number
}