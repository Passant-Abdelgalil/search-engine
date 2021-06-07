const router = require('express').Router();
const Word = require('../models/Word');
var stemmer = require('porter-stemmer').stemmer;


router.get('/', async(req, res) => {

    return res.render('homy', {
        css: 'style',
    })

});

router.post('/', async(req, res) => {
    
    console.log("req.body.wordName: ",req.body.wordName);
    var stemed_word=stemmer(req.body.wordName);
    console.log("stemiiinnng: "+stemed_word);
    res.redirect('/results/' + stemed_word +'/'+ 0);
});

module.exports = router; 
