const router = require('express').Router();
const checkFunc = require("../functions");

router.get('/', (req, res) => {

        return res.render('Home', {
            title: 'Home',
            css: 'style',
            user: req.user,
            message: req.flash('message')
        })

});
module.exports = router;
