const express = require('express');
const mongoose = require('mongoose');
const path = require('path');
const app = express(); 
const Word = require('./models/Word');
const bodyParser = require('body-parser');

const port = 7080; 


mongoose.connect('mongodb://localhost/APT_course', { useNewUrlParser: true, useUnifiedTopology: true });
const db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error: '));
db.once('open', () => {
    console.log('connected to DB: '+ db.name); 
});

app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.urlencoded({ extended: true }));
//body-parser middleware
app.use(bodyParser.json());



app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');
app.use(express.json());
app.use(express.urlencoded({ extended: true }));


app.get('/', async(req, res) => {

    // let newWord =  new Word({
    //     Word: "REEM",   
    //     pages:[{title:"REEM1"},{title:"REEM2"},{title:"REEM3"},
    //     {title:"REEM4"},{title:"REEM5"},{title:"REEM5"},
    //     {title:"REEM7"},{title:"REEM8"},{title:"REEM9"},
    //     {title:"REEM10"},{title:"REEM12"},{title:"REEM13"},
    //     {title:"REEM11"},{title:"REEM14"},{title:"REEM15"},
    //     {title:"REEM16"},{title:"REEM20"},{title:"REEM19"},
    //     {title:"REEM17"},{title:"REEM24"},{title:"REEM21"},
    //     {title:"REEM18"},{title:"REEM23"},{title:"REEM22"}
    // ]
    // });
    // newWord = await newWord.save();

    return res.render('homy', {
        css: 'style'
    })
});

app.use('/homy', require('./routes/homy'));
app.use('/results', require('./routes/results'));


app.listen(port, err => {
    if (err) return console.log(err); 
    console.log(`server started listening at ${port}`);
}); 