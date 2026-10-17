import path from 'path';
import { fileURLToPath } from 'url';
import webpack from 'webpack'
import HtmlWebpackPlugin from 'html-webpack-plugin';
import CopyPlugin from 'copy-webpack-plugin';


const __dirname = path.dirname(fileURLToPath(import.meta.url));

const PRODUCTION = false;

export default {
    entry: {
      students: path.resolve(__dirname, './javascript/students.js'),
      groups: path.resolve(__dirname, './javascript/groups.js')
    },
    output: {
        path: path.resolve(__dirname, '../server/public'),
        filename: 'javascript/[name]-bundle.js'
    },

    mode :  (PRODUCTION ? 'production' : 'development'),
    devtool : (PRODUCTION ? undefined : 'eval-source-map'),

    devServer: {
        static: {
            directory: path.resolve(__dirname, 'dist'),
            watch: true
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
                  name: '[name].[ext]',
                  outputPath: 'images'
                }
              }
            ]
          }
        ]
      },    
    
    plugins: [
        new webpack.ProgressPlugin(),
      new HtmlWebpackPlugin({
        template: './html/students.html',
        filename: './html/students.html',
        chunks: ['students'],
      }),
      new HtmlWebpackPlugin({
        template: './html/groups.html',
        filename: './html/groups.html',
        chunks: ['groups'],
      }),
        new CopyPlugin({
          patterns: [
            {
                from: path.resolve(__dirname, './html/index.html'),
                to: 'html/index.html'            
            },
            {
                from: path.resolve(__dirname, './images/*'),
                to: 'images/[name].[ext]'
            },
            {
                from: path.resolve(__dirname, './style/style.css'),
                to: 'style/style.css'
            }
          ]
        })      
    ],
};
