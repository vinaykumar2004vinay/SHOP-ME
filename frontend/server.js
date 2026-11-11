/**
 * server.js — Simple Node.js static file server for the frontend.
 * Serves all HTML, CSS, and JS files on http://localhost:3000
 *
 * Usage: node server.js
 *
 * This is needed because the frontend uses absolute paths (/css/style.css, /js/api.js)
 * which require a proper server — you can't just open index.html as a file.
 *
 * Alternative: Use VS Code Live Server extension (right-click index.html → Open with Live Server)
 */

const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;

// MIME type map — tells browser how to handle each file type
const MIME_TYPES = {
  '.html': 'text/html',
  '.css':  'text/css',
  '.js':   'application/javascript',
  '.json': 'application/json',
  '.png':  'image/png',
  '.jpg':  'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif':  'image/gif',
  '.svg':  'image/svg+xml',
  '.ico':  'image/x-icon',
};

const server = http.createServer((req, res) => {
  // Default route → index.html
  let urlPath = req.url === '/' ? '/index.html' : req.url;

  // Strip query strings (e.g., /index.html?search=shoes → /index.html)
  urlPath = urlPath.split('?')[0];

  const filePath = path.join(__dirname, urlPath);
  const ext = path.extname(filePath).toLowerCase();
  const contentType = MIME_TYPES[ext] || 'application/octet-stream';

  fs.readFile(filePath, (err, data) => {
    if (err) {
      if (err.code === 'ENOENT') {
        // 404 — file not found
        res.writeHead(404, { 'Content-Type': 'text/html' });
        res.end(`<h2>404 — Page Not Found</h2><p>Path: ${urlPath}</p><a href="/">Go Home</a>`);
      } else {
        // 500 — server error
        res.writeHead(500);
        res.end('Internal Server Error: ' + err.message);
      }
      return;
    }

    res.writeHead(200, { 'Content-Type': contentType });
    res.end(data);
  });
});

server.listen(PORT, () => {
  console.log('\n' + '='.repeat(55));
  console.log('  🛒  ShopEase Frontend Server RUNNING');
  console.log(`  URL: http://localhost:${PORT}`);
  console.log('  Serving files from: ' + __dirname);
  console.log('='.repeat(55) + '\n');
});
