'use strict';

angular.module('lyticalcnsApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


