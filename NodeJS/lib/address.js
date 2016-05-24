/**
 * Endereços de memória do modbus
 * Created by Raphael on 23/05/2016.
 */

module.exports = {
    CONSUMO_LIQUIDO : 0, //4
    CONSUMO_BRUTO : 8, //4
    BATERIA : 1, //4
    CONSUMO_DERIVADO : 2, //4
    RENOVAVEL_DERIVADO : 3, //4
    CONSUMO : {
        QUARTO : 4, //4
        SALA : 5, //4
        COZINHA : 6, //4
        AREA : 7 //4
    },
    BATTERY_STATUS : 9, //1
    SW : {
        QUARTO : 0, //1
        SALA : 1, //1
        COZINHA : 2, //1
        AREA : 3 //1
    },
    FROM : {
        QUARTO : 4, //1
        SALA : 5, //1
        COZINHA : 6, //1
        AREA : 7 //1
    }
};