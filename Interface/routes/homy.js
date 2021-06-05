const router = require('express').Router();
const Word = require('../models/Word');

router.get('/', async(req, res) => {

    return res.render('homy', {
        css: 'style',
    })

});

router.post('/', async(req, res) => {
 
    res.redirect('/results/' + req.body.wordName +'/'+ 0);

});

module.exports = router;
