const fs = require('fs');

const inputFile = './dist/output.css';
const outputFile = './dist/extracted.css';

const layoutProperties = [
    'width', 'height', 'min-height', 'max-width',
    'position', 'top', 'left', 'display',
    'flex', 'grid', 'margin', 'padding'
].join('|');

const selectorsToExtract = ['nav', 'main', 'header'];
const escapedSelectors = selectorsToExtract
    .map(selector => selector.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&'))
    .join('|');

const regexPatterns = new RegExp(`\\b(${escapedSelectors})\\b[^{]*{[^}]*(?:${layoutProperties})[^}]*}`, 'gi');

fs.readFile(inputFile, 'utf8', (err, data) => {
    if (err) {
        console.error('Error:', err.message);
        process.exit(1);
    }

    const extractedRules = data.match(regexPatterns);

    if (!extractedRules?.length) {
        console.error('No layout rules found');
        process.exit(1);
    }

    fs.writeFile(outputFile, extractedRules.join('\n\n'), 'utf8', (writeErr) => {
        if (writeErr) {
            console.error('Write error:', writeErr.message);
            process.exit(1);
        }
        console.log('Extracted to:', outputFile);
    });
});