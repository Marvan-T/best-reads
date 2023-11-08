const fs = require('fs');

module.exports = {
  devServer: {
    port: 3000,
    https: {
      key: fs.readFileSync('./local-certs/localhost+1-key.pem'),
      cert: fs.readFileSync('./local-certs/localhost+1.pem'),
    },
    proxy: {
      '/api/extensions': {
        target: 'http://localhost:5208',
        ws: true,
        changeOrigin: true
      },
      '/api': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true
      }
    }
  }
}
