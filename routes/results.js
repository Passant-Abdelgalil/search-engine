const router = require('express').Router();
const Word = require('../models/Word');

router.get('/:theWord', async(req, res) => {

    const word = await Word.findOne({ Word: req.params.theWord });

    return res.render('results', {
        title: word.Word+' Results',
        css: 'style',
        word: word,
        pages: word.pages
    })
 
}); 

router.post('/', async(req, res) => {

    res.redirect('/results/' + req.body.wordName);


});

module.exports = router;