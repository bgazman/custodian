import { defineConfig } from 'orval';

export default defineConfig({
    api: {
        input: {
            target: 'http://localhost:8080/v3/api-docs',
        },
        output: {
            mode: 'tags-split',
            target: './src/api/generated/index.ts',
            client: 'react-query',
            baseUrl: 'http://localhost:8080',
            override: {
                mutator: {
                    path: './src/api/common/SecureApiClient.ts',
                    name: 'customFetcher'
                },
                query: {
                    useQuery: true,
                    useInfinite: true
                },
                operations: {
                    getUsers: {
                        query: {
                            useQuery: true
                        }
                    }
                }
            }
        }
    }
});