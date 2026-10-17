import path from 'path';
import { fileURLToPath } from 'url';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import CopyPlugin from 'copy-webpack-plugin';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const PRODUCTION = false;

export default {
    entry: {
      admin: path.resolve(__dirname, 'client/admin-vote/index.admin.js'),
      votant: path.resolve(__dirname, 'client/votant/index.votant.js')
    },
    output: {
        path: path.resolve(__dirname, 'server/public'),
        filename: 'javascripts/[name]-bundle.js'
    },

    mode :  (PRODUCTION ? 'production' : 'development'),
    devtool : (PRODUCTION ? undefined : 'eval-source-map'),

    devServer: {
        static: {
            directory: path.resolve(__dirname, './server/public'),
        },
        host: 'localhost',
        port : 8888,
        open : true
    },
  
    module: {
        rules: [
          {
            test: /\.m?js*/,
            exclude: /(node_modules)/,
            use: {
              loader: 'babel-loader',
              options: {
                presets: [
                  '@babel/preset-env',
                ],
                plugins: [
                ]
              }
            }
          },
          {
            test: /\.css$/,
            use: [
              { loader: 'style-loader' },
              { loader: 'css-loader' }
            ]
          },
          {
            test: /\.(png|jpg|gif)/i,
            use: [
              {
                loader: 'file-loader',
                options: {
                  name: 'images/[name].[ext]',
                  outputPath: 'images'
                }
              }
            ]
          }
        ]
      },    
    
    plugins: [
      new HtmlWebpackPlugin({
        template: 'client/html/admin-vote.html',
        filename: 'html/admin-vote.html',
        chunks: ['admin'],
      }),
      new HtmlWebpackPlugin({
        template: 'client/html/votant.html',
        filename: 'html/votant.html',
        chunks: ['votant'],
      }),
      new HtmlWebpackPlugin({
        template: 'client/html/about.html',    // Ajout du template about
        filename: 'html/about.html',
        chunks: ['about']
      }),
      new HtmlWebpackPlugin({
          template: 'client/html/index.html',    // Ajout du template index
          filename: 'html/index.html',
          chunks: ['index']
      }),
        new CopyPlugin({
          patterns: [
            {
                from: path.resolve(__dirname, 'client/images'),
                to: 'images'
            },
            {
                from: path.resolve(__dirname, 'client/style'),
                to: 'style'
            }
          ]
        })      
    ],
};
