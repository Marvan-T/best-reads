const fs = require('fs');

module.exports = {
  devServer: {
    port: 3000,
    https: {
      key: fs.readFileSync('./local-certs/localhost+1-key.pem'),
      cert: fs.readFileSync('./local-certs/localhost+1.pem'),
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true
      }
    }
  }
}
