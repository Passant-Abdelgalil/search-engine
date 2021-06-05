
const mongoose = require('mongoose');
const Schema = mongoose.Schema;

 
const WordSchema = new Schema({

    Word:{
        type: String
    },
    IDF:{
        type: Number
    },
    pages:[{
        title:String,
        URL: String,
        Sentance:[String],
        tag:[String],
        TF:[Number]
    }] 
   
});

module.exports = mongoose.model('Word', WordSchema); 