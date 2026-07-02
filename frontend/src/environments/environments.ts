/**
 * ENVIRONMENTS
 *      - initialize variables we will use across the application
 *      - only information your application needs, not DATA that it will use
 *          - ex: endpoint URLs, feature flags, logging/monitoring options, etc.
 *          - NO: secrets - file will likely be public
 * 
 *      - this is an example of a dev environments file
 *          - for prod: environments.prod.ts file - it will look the same just with different values
 */
export const environment = {
    production: false, 
    baseApiUrl: "http://localhost:8081"
}