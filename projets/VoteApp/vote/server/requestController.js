import * as fs from 'fs/promises';
import path from 'path';
import { getContentTypeFrom }  from './script/contentTypeUtil.js';

const BASE = 'http://localhost/';

/**
 * Controller class that handles HTTP requests and responses
 */
export default class RequestController {

  #request;
  #response; 
  #url;

  /**
   * Creates a new RequestController instance
   * @param request - The HTTP request object
   * @param response - The HTTP response object
   */
  constructor(request, response) {
    this.#request = request,
    this.#response = response;
    this.#url = new URL(this.request.url,BASE).pathname;
  }

  /**
   * Gets the response object
   * @returns The response object
   */
  get response() {
    return this.#response;
  }

  /**
   * Gets the request object
   * @returns The request object
   */
  get request() {
    return this.#request;
  }

  /**
   * Gets the URL pathname
   * @returns {string} The URL pathname
   */
  get url() {
    return this.#url;
  }

  /**
   * Handles the incoming HTTP request
   * Sets appropriate headers, builds the response and ends it
   */
  async handleRequest() {
    this.response.setHeader("Content-Type" , getContentTypeFrom(this.url) );
    await this.buildResponse();
    this.response.end();
  }

  /**
   * Sends a file as the response
   * @param {string} filePath - Path to the file to send
   */
  async sendFile(filePath) {
    try {
        await fs.access(filePath);
        const data = await fs.readFile(filePath);
        this.response.statusCode = 200;
        this.response.end(data);
    } catch (error) {
        this.response.statusCode = 404;
        this.response.end('Fichier non trouvé');
    }
}

  /**
   * Builds the response by routing the request to the appropriate file
   * Sends the requested resource if it exists, else responds with a 404
   */
  async buildResponse()  {
    const url = new URL(this.#request.url, `http://${this.#request.headers.host}`).pathname;

    if (url === '/') {
      await this.sendFile('server/public/html/index.html');
    } else if (url === '/admin-vote') {
      await this.sendFile('server/public/html/admin-vote.html');
    } else if (url === '/votant') {
      await this.sendFile('server/public/html/votant.html');
    } else if (url === '/about') {
      await this.sendFile('server/public/html/about.html');
    } else {
        const filePath = path.join('server','public', url.slice(1));
        await this.sendFile(filePath);
    }
  }

}
