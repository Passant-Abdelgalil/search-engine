const router = require('express').Router();
const Word = require('../models/Word');
const Word_Frequency = require('../models/Word_Frequency');

router.get('/:theWord/:NoPage', async(req, res) => {

    var word="";
    var arrFreqWords=[];

    try {
        
        word = await Word.findOne({ Word: req.params.theWord });

        // <%var k=0;%>
        // <%for(k=0; k< arr.length; k++){%>
        //   <%console.log("global_Word_Frequency: ", arr[k]);}%>
        // console.log(req.params.theWord);

        // db.collection("wordfreqs").find({}).toArray(function(err, result) {
        //     if (err) throw err;
        //     for(var i=0; i< result.length; i++){
        //         arrFreqWords[i]=result[i].Word;
        //         console.log(arrFreqWords[i]);
        //     }
        //   })
        arrFreqWords = await Word_Frequency.find({});
        // for(var i=0; i< arrFreqWords.length; i++)
        //     console.log("arrFreqWords: ",arrFreqWords[i]);

        const wordfreqq = await Word_Frequency.findOne({ Word: req.params.theWord });
        if(! wordfreqq && word)
        {
            let newWord =  new Word_Frequency({
                Word: req.params.theWord 
            });
            newWord = await newWord.save();  
            // console.log("newWord: ",newWord);
        }
        global_Word_Frequency= await Word_Frequency.find({});
        var arr=[];
        
        for(var k=0; k< global_Word_Frequency.length; k++)
            arr[k]=global_Word_Frequency[k].Word;

        // console.log(arr.length);

        // $( "#search" ).autocomplete({
        //     source: global_Word_Frequency
           
        //   });

        if(word)
        { 
            // for(var i=0; i< arrFreqWords.length; i++)
            //     console.log("arrFreqWords: ",arrFreqWords[i]);  

            return res.render('results', {
                title: word.Word+' Results',
                css: 'style', 
                word: word,
                pages: word.pages,   
                NoPage: req.params.NoPage ,
                arr: arr
            }) 
        }
        

        else{
            return res.render('results', {
                title: 'No Results',
                css: 'style', 
                word: null,
            })
        }
    } catch (error) {
         
    }
}); 


router.post('/', async(req, res) => {

    res.redirect('/results/' + req.body.wordName +'/' + 0);


});

module.exports = router;