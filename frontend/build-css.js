// build-css.js
import { exec } from 'child_process'

exec('npx tailwindcss -i ./src/index.css -o ./dist/output.css --minify',
    (error, stdout, stderr) => {
        if (error) console.error(error)
        else console.log('CSS generated successfully')
    })