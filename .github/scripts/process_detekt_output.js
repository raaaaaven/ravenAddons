const fs = require('fs');
const path = require('path');

const PR_SHA = process.env.PR_SHA;
const GITHUB_REPOSITORY = process.env.GITHUB_REPOSITORY;

// Read detekt_output.txt
const detektOutput = fs.readFileSync('detekt_output.txt', 'utf8');

const lines = detektOutput.split('\n');
const lineLength = lines.length - 1; // We might have an empty line at the end maybe?

let comment = `### ${lineLength} Detekt Failure${lineLength !== 1 ? 's were' : ' was'} detected:\n\n`;

lines.forEach((line) => {
    if (!line.trim()) return; // Skip empty lines

    // Extract file path and line number
    const filePathMatch = line.match(/file=([^,]+)/);
    const lineNumberMatch = line.match(/line=(\d+)/);
    if (!filePathMatch || !lineNumberMatch) return;

    const filePath = filePathMatch[1];
    const lineNumber = lineNumberMatch[1];

    // Remove everything before 'src/' in the file path (if it exists)
    const srcIndex = filePath.indexOf('src/');
    const cleanedFilePath = srcIndex !== -1 ? filePath.substring(srcIndex) : filePath;

    // Extract file name
    const fileName = path.basename(cleanedFilePath);

    // Clean up the line to remove everything between '::' and '::' (inclusive)
    const cleanMessage = line.replace(/::.*?::/g, '');

    // Append to comment
    comment += `- [${fileName}#L${lineNumber}](https://github.com/${GITHUB_REPOSITORY}/blob/${PR_SHA}/${cleanedFilePath}#L${lineNumber}): ${cleanMessage}\n`;
});

// Write comment to file
fs.writeFileSync('detekt_comment.txt', comment);
