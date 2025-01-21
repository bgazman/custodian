const fs = require('fs')
const css = fs.readFileSync('./dist/output.css', 'utf8')

// Extract layout-related styles
const layoutStyles = css.match(/(header|main|footer)\s*{[^}]+}/g)
console.log(layoutStyles)