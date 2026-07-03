export enum SourceType {
    TRADITIONAL_401K = 'TRADITIONAL_401K',
    ROTH_401K = 'ROTH_401K',
    TRADITIONAL_IRA = 'TRADITIONAL_IRA',
    ROTH_IRA = 'ROTH_IRA',
    SEP_IRA = 'SEP_IRA',
    TAXABLE_BROKERAGE = 'TAXABLE_BROKERAGE'
}

export const SourceTypeLabels: Record<SourceType, string> = {
    [SourceType.TRADITIONAL_401K]: 'Traditional 401K',
    [SourceType.ROTH_401K]: 'Roth 401K',
    [SourceType.TRADITIONAL_IRA]: 'Traditional IRA',
    [SourceType.ROTH_IRA]: 'Roth IRA',
    [SourceType.SEP_IRA]: 'SEP IRA',
    [SourceType.TAXABLE_BROKERAGE]: 'Taxable Brokerage'
}