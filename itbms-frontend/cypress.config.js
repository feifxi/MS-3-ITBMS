import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    specPattern: 'cypress/e2e/**/*.{cy,spec}.{js,jsx,ts,tsx}',
    // baseUrl: 'http://intproj24.sit.kmutt.ac.th/ms3',
    // baseAPI: 'http://intproj24.sit.kmutt.ac.th/ms3/itb-mshop',
    // baseUrl: 'http://ip24ms3.sit.kmutt.ac.th',
    // baseAPI: 'http://ip24ms3.sit.kmutt.ac.th/itb-mshop',
    baseUrl: 'http://localhost:5173',
    baseAPI: 'http://localhost:8080/itb-mshop',
    experimentalRunAllSpecs: true,
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
});
