const fs = require('fs');
const path = require('path');
const { createServer } = require('http');
const next = require('next');

const port = Number(process.env.PORT || 3000);
const dev = false;
const app = next({ dev, dir: __dirname });
const handle = app.getRequestHandler();

const MIME_TYPES = {
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.woff2': 'font/woff2',
  '.woff': 'font/woff',
  '.ttf': 'font/ttf',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.webp': 'image/webp',
  '.map': 'application/json; charset=utf-8',
};

function sendNotFound(res) {
  res.statusCode = 404;
  res.setHeader('Content-Type', 'text/plain; charset=utf-8');
  res.end('Not Found');
}

function sendFile(res, filePath) {
  const ext = path.extname(filePath).toLowerCase();
  const contentType = MIME_TYPES[ext] || 'application/octet-stream';
  res.statusCode = 200;
  res.setHeader('Content-Type', contentType);
  res.setHeader('Cache-Control', 'public, max-age=31536000, immutable');
  fs.createReadStream(filePath).pipe(res);
}

app.prepare().then(() => {
  createServer((req, res) => {
    try {
      const url = decodeURIComponent((req.url || '/').split('?')[0]);

      if (url === '/favicon.ico') {
        const faviconPath = path.join(__dirname, 'public', 'favicon.ico');
        if (fs.existsSync(faviconPath)) {
          sendFile(res, faviconPath);
        } else {
          sendNotFound(res);
        }
        return;
      }

      if (url.startsWith('/_next/static/')) {
        const relativePath = url.replace('/_next/static/', '');
        const staticPath = path.join(__dirname, '.next', 'static', relativePath);
        if (fs.existsSync(staticPath) && fs.statSync(staticPath).isFile()) {
          sendFile(res, staticPath);
          return;
        }
        sendNotFound(res);
        return;
      }

      handle(req, res);
    } catch (error) {
      console.error('Custom server error:', error);
      res.statusCode = 500;
      res.end('Internal Server Error');
    }
  }).listen(port, (err) => {
    if (err) throw err;
    console.log(`> Ready on http://localhost:${port}`);
  });
});
