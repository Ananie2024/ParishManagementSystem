// ==========================================
// PARISH MANAGEMENT SYSTEM - TYPESCRIPT
// ==========================================
// This file handles all application logic:
// - Fetching data from Spring Boot API
// - User interactions (form submission)
// - Updating the user interface (DOM manipulation)
// - Error handling and validation
// ==========================================
// CONFIGURATION CONSTANTS
// ==========================================
/*
Constants are variables that never change
Use 'const' instead of 'let' when value won't be reassigned

Naming convention: SCREAMING_SNAKE_CASE for constants
*/
/**
* Base URL for the Spring Boot API
* Change this if your backend runs on a different port
*/
const API_BASE_URL = 'http://localhost:8080';
/*
: string is a type annotation
Tells TypeScript this variable must be a string
TypeScript will error if you try: API_BASE_URL = 123
*/
// ==========================================
// API COMMUNICATION FUNCTIONS
// ==========================================
/*
These functions handle communication with the Spring Boot backend
They use the Fetch API (built into modern browsers)
*/
/**
* Generic function to fetch data from any API endpoint
*
* @template T - The type of data expected in response.data
* @param {string} endpoint - API endpoint path (e.g., '/hello')
* @returns {Promise<ApiResponse<T>>} - Promise that resolves to API response
*
* EXAMPLE USAGE:
* const response = await fetchFromAPI<WelcomeResponse>('/hello');
*/
async function fetchFromAPI(endpoint) {
    /*
    FUNCTION SIGNATURE BREAKDOWN:
    - async: This function returns a Promise (can use 'await' inside)
    - <T>: Generic type parameter (function works with any type)
    - (endpoint: string): Parameter with type annotation
    - Promise<ApiResponse<T>>: Return type (a Promise wrapping ApiResponse)
    
    WHY ASYNC?
    - Network requests take time (can't block the UI)
    - async/await makes async code look synchronous
    - Easier to read than callbacks or .then() chains
    */
    try {
        /*
        try/catch is error handling
        - try: Code that might throw an error
        - catch: Code to run if error occurs
        - Prevents app from crashing on errors
        */
        // Make HTTP GET request to the API
        const response = await fetch(`${API_BASE_URL}${endpoint}`);
        /*
        fetch() is a browser API for HTTP requests
        - Takes a URL as parameter
        - Returns a Promise<Response>
        - await pauses execution until Promise resolves
        
        Template literal (`${var}${var2}`) combines strings
        Example: `${API_BASE_URL}${endpoint}` → 'http://localhost:8080/hello'
        
        Response type is the return type of fetch()
        */
        // Check if HTTP request was successful
        if (!response.ok) {
            /*
              response.ok is true for status codes 200-299
              response.ok is false for 400, 404, 500, etc.

              ! is the NOT operator (inverts boolean)
            */
            // Throw an error if request failed
            throw new Error(`HTTP error! status: ${response.status}`);
            /*
              throw creates an error and jumps to catch block
              Error object contains error message
              response.status is the HTTP status code (404, 500, etc.)
            */
        }
        // Parse JSON response body
        const data = await response.json();
        /*
          response.json() parses JSON string to JavaScript object
          Also returns a Promise, so we await it

          Type annotation ensures data matches ApiResponse<T> structure
        */
        // Return the parsed data
        return data;
    }
    catch (error) {
        /*
          Catch block handles any errors from try block
          error parameter contains the error object
        */
        // Log error to browser console (for debugging)
        console.error('API Error:', error);
        /*
          console.error() prints to browser DevTools console
          Red color in console helps identify errors
          Always log errors during development!
        */
        // Return an error response object
        return {
            status: 'error',
            message: error instanceof Error ? error.message : 'Unknown error occurred'
            /*
              error instanceof Error checks if error is an Error object
              If yes: use error.message (the error text)
              If no: use generic message

              This is a ternary operator (short if/else):
              condition ? valueIfTrue : valueIfFalse
            */
        };
    }
}
/**
 * Fetch data from API with query parameters
 *
 * @template T - Expected response data type
 * @param {string} endpoint - API endpoint path
 * @param {Record<string, string>} params - Query parameters as key-value pairs
 * @returns {Promise<ApiResponse<T>>} - API response
 *
 * EXAMPLE USAGE:
 * const response = await fetchWithParams<GreetingResponse>(
 *     '/hello/greet',
 *     { name: 'John' }
 * );
 * This calls: /hello/greet?name=John
 */
async function fetchWithParams(endpoint, params
/*
  Record<string, string> is a TypeScript utility type
  Means: object with string keys and string values
  Example: { name: 'John', age: '25' }

  Similar to: { [key: string]: string }
*/
) {
    try {
        // Build query string from params object
        const queryString = new URLSearchParams(params).toString();
        /*
          URLSearchParams is a browser API for building query strings

          Example:
          params = { name: 'John', city: 'Kigali' }
          queryString = 'name=John&city=Kigali'

          .toString() converts URLSearchParams object to string
        */
        // Construct full URL with query parameters
        const url = `${API_BASE_URL}${endpoint}?${queryString}`;
        /*
          Template literal combines all parts
          Example: 'http://localhost:8080/hello/greet?name=John'
        */
        // Make the HTTP request
        const response = await fetch(url);
        // Check if request was successful
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        // Parse and return JSON response
        const data = await response.json();
        return data;
    }
    catch (error) {
        // Handle errors same as fetchFromAPI
        console.error('API Error:', error);
        return {
            status: 'error',
            message: error instanceof Error ? error.message : 'Unknown error occurred'
        };
    }
}
// ==========================================
// DOM MANIPULATION FUNCTIONS
// ==========================================
/*
  DOM (Document Object Model) = tree structure of HTML elements
  These functions find and modify HTML elements on the page
*/
/**
 * Loads and displays welcome message when page loads
 * Called automatically on page load
 *
 * @returns {Promise<void>} - Promise with no return value
 */
async function loadWelcomeMessage() {
    /*
      Promise<void> means:
      - Function is async (returns a Promise)
      - But doesn't return any useful value
      - Just performs side effects (updates UI)
    */
    // Find the HTML element where we'll display the message
    const welcomeElement = document.getElementById('welcome-message');
    /*
      document.getElementById() finds element by ID attribute

      Return type: HTMLElement | null
      - HTMLElement if element exists
      - null if element not found

      | is union type (can be one type OR another)
    */
    // Check if element exists (defensive programming)
    if (!welcomeElement) {
        /*
          Always check if element exists before using it
          Prevents "Cannot read property of null" errors
        */
        console.error('Welcome message element not found');
        return; // Exit function early (don't continue)
    }
    // Fetch welcome message from API
    const response = await fetchFromAPI('/hello');
    /*
      <WelcomeResponse> tells TypeScript what type response.data will be
      This enables autocomplete for response.data.welcomeMessage
    */
    // Check if API call was successful
    if (response.status === 'success' && response.data) {
        /*
          && is AND operator (both conditions must be true)

          Why check response.data?
          - data is optional (data?: T)
          - Could be undefined even if status is 'success'
          - Checking prevents errors when accessing response.data.welcomeMessage
        */
        // Update element's text content
        welcomeElement.textContent = response.data.welcomeMessage;
        /*
          textContent sets the text inside an element
          Alternative: innerHTML (can include HTML tags, but less safe)

          This replaces "Loading..." with the actual message
        */
        // Add animation class (optional styling enhancement)
        welcomeElement.classList.add('fade-in');
        /*
          classList is an API for manipulating CSS classes
          Methods:
          - .add('class-name'): Adds a class
          - .remove('class-name'): Removes a class
          - .toggle('class-name'): Adds if absent, removes if present
          - .contains('class-name'): Checks if class exists
        */
    }
    else {
        // API call failed - show error message
        welcomeElement.textContent = 'Failed to load welcome message';
        welcomeElement.classList.add('text-red-600');
        /*
          Adds Tailwind CSS class for red text
          Could also use: welcomeElement.style.color = 'red';
        */
    }
}
/**
 * Handles form submission when user clicks "Get Greeting"
 * Prevents page reload, validates input, and calls API
 *
 * @param {Event} event - Form submit event
 * @returns {Promise<void>} - Promise with no return value
 */
async function handleGreetingSubmit(event) {
    /*
      Event is the base type for all browser events
      More specific types: MouseEvent, KeyboardEvent, FormEvent
    */
    // Prevent default form submission behavior
    event.preventDefault();
    /*
      By default, submitting a form:
      1. Sends data to server
      2. Reloads the page

      .preventDefault() stops this behavior
      We handle submission with JavaScript instead
    */
    // Get reference to the form element
    const form = event.target;
    /*
      event.target is the element that triggered the event

      Type assertion (as HTMLFormElement):
      - TypeScript doesn't know specific type of event.target
      - 'as' tells TypeScript "trust me, this is HTMLFormElement"
      - Enables access to form-specific properties

      Without 'as': TypeScript error "Property 'value' does not exist on type 'EventTarget'"
    */
    // Get the name input element
    const nameInput = document.getElementById('name-input');
    /*
      HTMLInputElement is the specific type for <input> elements
      Allows access to input-specific properties like .value
    */
    // Get the trimmed input value
    const name = nameInput.value.trim();
    /*
      .value gets the text typed in the input
      .trim() removes whitespace from start and end

      Example:
      '  John  ' → 'John'
      '   ' → '' (empty string)
    */
    // Validate input - check if name is empty
    if (!name) {
        /*
          Empty string is falsy in JavaScript
          !name is true if name is empty

          Falsy values: '', 0, false, null, undefined, NaN
          Truthy values: everything else
        */
        showError('Please enter your name');
        return; // Exit function - don't call API
    }
    // Hide any previous messages
    hideGreeting();
    hideError();
    /*
      Clear previous results before showing new ones
      Prevents confusion with old messages
    */
    // Fetch greeting from API with user's name
    const response = await fetchWithParams('/hello/greet', { name: name }
    /*
      Object shorthand: { name: name } can be written as { name }
      When key and value have same name
    */
    );
    // Check if API call was successful
    if (response.status === 'success' && response.data) {
        // Show the greeting message
        showGreeting(response.data.greeting);
    }
    else {
        // Show error message
        showError(response.message || 'Failed to get greeting');
        /*
          || is OR operator (logical OR)
          If response.message is falsy (undefined, null, ''), use default message
          This is called "fallback value" pattern
        */
    }
}
/**
 * Displays a greeting message in the green success box
 *
 * @param {string} message - The greeting message to display
 * @returns {void} - No return value
 */
function showGreeting(message) {
    /*
      Regular function (not async) because it doesn't need to wait for anything
      void return type means function doesn't return a value
    */
    // Get references to the elements
    const greetingResponse = document.getElementById('greeting-response');
    const greetingText = document.getElementById('greeting-text');
    // Check if elements exist
    if (!greetingResponse || !greetingText) {
        /*
          || is OR operator (at least one must be true)
          If EITHER element is null, condition is true
        */
        console.error('Greeting elements not found');
        return;
    }
    // Update the text content
    greetingText.textContent = message;
    // Make the element visible
    greetingResponse.classList.remove('hidden');
    /*
      .remove() removes the 'hidden' CSS class
      The 'hidden' class has: display: none;
      Removing it makes the element visible
    */
}
/**
 * Hides the greeting message box
 *
 * @returns {void} - No return value
 */
function hideGreeting() {
    const greetingResponse = document.getElementById('greeting-response');
    if (greetingResponse) {
        // Add 'hidden' class to hide element
        greetingResponse.classList.add('hidden');
    }
}
/**
 * Displays an error message in the red error box
 *
 * @param {string} message - The error message to display
 * @returns {void} - No return value
 */
function showError(message) {
    const errorMessage = document.getElementById('error-message');
    const errorText = document.getElementById('error-text');
    if (!errorMessage || !errorText) {
        console.error('Error elements not found');
        return;
    }
    // Update text and make visible
    errorText.textContent = message;
    errorMessage.classList.remove('hidden');
}
/**
 * Hides the error message box
 *
 * @returns {void} - No return value
 */
function hideError() {
    const errorMessage = document.getElementById('error-message');
    if (errorMessage) {
        errorMessage.classList.add('hidden');
    }
}
// ==========================================
// EVENT LISTENERS & INITIALIZATION
// ==========================================
/*
  Event listeners "listen" for user actions (clicks, typing, etc.)
  When action occurs, they run a function
*/
/**
 * Initializes the application
 * Sets up event listeners and loads initial data
 * This is the entry point - everything starts here
 *
 * @returns {void} - No return value
 */
function initializeApp() {
    /*
      This function runs once when page loads
      Sets up everything needed for the app to work
    */
    console.log('Initializing Parish Management System...');
    /*
      Log to console for debugging
      Confirms JavaScript is running
      Check browser DevTools Console tab (F12)
    */
    // Load the welcome message
    loadWelcomeMessage();
    /*
      Calls async function but doesn't await
      Function runs in background while rest of code continues

      Could also write: await loadWelcomeMessage();
      But would need to make initializeApp async
    */
    // Set up form submission handler
    const greetingForm = document.getElementById('greeting-form');
    if (greetingForm) {
        // Attach event listener to form
        greetingForm.addEventListener('submit', handleGreetingSubmit);
        /*
          addEventListener attaches a function to an event

          Syntax: element.addEventListener(eventType, handlerFunction)

          Common events:
          - 'click': Mouse click
          - 'submit': Form submission
          - 'input': Text input changes
          - 'keydown': Key pressed
          - 'load': Resource loaded

          Handler function receives Event object as parameter
          Event object contains info about what happened
        */
    }
    else {
        console.error('Greeting form not found');
    }
    console.log('Application initialized successfully!');
}
// ==========================================
// APPLICATION START
// ==========================================
/*
  This code runs when the script loads
  Sets up the initial event listener for page load
*/
// Wait for DOM to be ready before initializing
document.addEventListener('DOMContentLoaded', initializeApp);
/*
  DOMContentLoaded event fires when:
  - HTML is fully parsed
  - DOM tree is complete
  - BUT external resources (images, stylesheets) may still be loading

  This ensures HTML elements exist before we try to access them

  ALTERNATIVE: window.addEventListener('load', initializeApp);
  'load' event waits for ALL resources (slower but more complete)
*/
// ==========================================
// TYPESCRIPT COMPILATION NOTES
// ==========================================
/*
  TO COMPILE THIS FILE:
  1. Install TypeScript globally:
     npm install -g typescript

  2. Compile the file:
     tsc app.ts

     This creates app.js that browsers can run

  3. For automatic compilation (development):
     tsc app.ts --watch

     Recompiles automatically when you save changes

  4. TypeScript configuration file (optional):
     Create tsconfig.json for project-wide settings:
     tsc --init
*/
// ==========================================
// DEBUGGING TIPS
// ==========================================
/*
  BROWSER DEVTOOLS (Press F12):

  1. CONSOLE TAB:
     - View console.log() and console.error() output
     - See JavaScript errors
     - Test code: Type JavaScript and press Enter

  2. NETWORK TAB:
     - See all HTTP requests
     - Check if API calls succeed (status 200)
     - View request/response data
     - Look for CORS errors

  3. SOURCES TAB:
     - Set breakpoints (click line number)
     - Pause code execution
     - Step through code line by line
     - Inspect variable values

  4. ELEMENTS TAB:
     - Inspect HTML structure
     - View applied CSS styles
     - Edit HTML/CSS live
     - See event listeners on elements

  COMMON ERRORS:
  - "Cannot read property 'X' of null": Element not found
  - "CORS error": Backend CORS not configured
  - "404 Not Found": Wrong API endpoint URL
  - "Unexpected token": JSON parsing error
*/
// ==========================================
// TYPESCRIPT CONCEPTS SUMMARY
// ==========================================
/*
  TYPE ANNOTATIONS:
  const name: string = 'John';
  function greet(name: string): string { }

  INTERFACES:
  interface User {
    name: string;
    age: number;
  }

  GENERICS:
  function identity<T>(arg: T): T {
    return arg;
  }

  UNION TYPES:
  let value: string | number;
  value = 'hello';  // OK
  value = 42;       // OK
  value = true;     // Error

  TYPE ASSERTIONS:
  const input = document.getElementById('name') as HTMLInputElement;

  OPTIONAL PROPERTIES:
  interface Config {
    required: string;
    optional?: number;
  }

  ASYNC/AWAIT:
  async function fetchData() {
    const response = await fetch(url);
    return response.json();
  }

  PROMISES:
  Promise<string> = will eventually resolve to string
  Promise<void> = will eventually complete (no value)

  NULL CHECKS:
  if (element) { * element is not null * }
  element?.property  // Optional chaining (safe navigation)
*/ 
