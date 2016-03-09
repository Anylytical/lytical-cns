 'use strict';

angular.module('lyticalcnsApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-lyticalcnsApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-lyticalcnsApp-params')});
                }
                return response;
            }
        };
    });
