module.exports = function (config) {
    config.set({

        basePath: '../.',
        files: [
            'lib/angular/angular.js',
            'lib/angular/angular.js',
            'lib/angular/angular-loader.js',
            'lib/angular/angular-resource.js',
            'lib/angular/angular-sanitize.js',
            'lib/angular/angular-mocks.js',
            'js/*.js',
            'unit-tests/**/*.js'
        ],

        //  export PHANTOMJS_BIN=/Users/florian/opt/phantomjs-1.8.1-macosx/bin/phantomjs
        browsers: ['PhantomJS', 'Chrome'],
        reporters: ['progress', 'junit', 'coverage', 'remote'],
        frameworks: ["jasmine"],
        autoWatch: true,
        singleRun: false,
        junitReporter: {
            outputFile: 'target/test_out/unit.xml',
            suite: 'unit'
        },
        remoteReporter: {
            host: 'localhost',
            port: '9000'
        },
        plugins: [
            'karma-jasmine',
            'karma-coverage',
            'karma-chrome-launcher',
            'karma-phantomjs-launcher',
            'karma-junit-reporter',
            'karma-remote-reporter'
        ]
    });
};