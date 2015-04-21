# ![alt text][Logo]
#### Motivation
It has been recognized that poor data quality can have multiple negative impact to
enterprises [1]. Businesses operating on dirty data are in risk of causing large amount of
financial loses. Maintaining data quality can also increases operational cost as business
would need to spend time and resources to detect erroneous data and correct them. As data grows bigger these days, data repairing has became an important problem
and an important research area.

DTCleaner produces multi-target decision trees for the purpose of data cleaning. It's built for 
detecting erroneous tuples in the dataset based on given set of conditional functional dependencies (CFDs) and building a classification model to predict erroneous tuples such that the "cleaned" dataset satisfies the CFDs, and semantically correct. 

#### Example
Consider the following schema:
```js
 hosp(ProviderNum, HospName, Addr, City, State, ZIP, County, Phone, HospType, 
      HospOwner, EmergencySerivce, Condition, MeasureName, StateAvg).
```
The data of this schema was taken from the US Department of Health & Human
Services website. Here a hosp tuple contains 14 values attributes describing provider-
level data for measures of different care ( heart attack care, heart failure care, surgical
care, ...) and the following conditional functional dependencies (CFDs) used to detect
erroneous tuples
```js
CFD1 : hosp([Zip = 36545] -> [City = Jackson])
CFD2 : hosp([Zip = 94115] -> [City = San Francisco])
```
where CFD1 (resp. CFD2) asserts that if the zip code is 94115 (resp. 36545), then the city
name must be San Francisco (resp. Jackson).

...  | HospName | Addr | City | State | Zip | ...
---  | -------- | ---- |----- | ------| ----| ---
 ... | Jackson Medical Ctr  | 220 Hospital Drive | Jackson | AL | 36545
...  | Jackson Medical Ctr  | 220 Hospital Drive | ***Jakson*** | AL | ***36545***
... | SanFran Hospital | 1001 Potrero Ave | San Francisco | CA | 94110
... |Cali Pacific Medical Ctr | 3555 Cesar St | ***San Fran*** | CA | ***94110*** 

Consider tuple 2 and 4. Tuple 2 (resp. 4) satisfies the premise of CFD1
(resp. CFD2) but they disagree in the right hand side (RHS) values (the city is
misspelled in the case of tuple 2 and shortened in the case of tuple 4). At this point we
know that the entries in t2[City], t2[Zip], t4[City], t4[Zip] are dirty and need to be cleaned. 
The problem is more complicated than simply changing the values of city to match the RHS values of the CFD,
because we are unsure which attribute is the wrong one (zip code, city, or maybe both) in the first place! 

#### DTCleaner
The system takes in the following inputs:

1. A clean dataset that is assumed to be clean and satisfies all the CFDs. We use this
dataset to test the accuracy of the predicted values after the prediction process.
2. A dirty dataset that matches with the clean dataset in terms of its attributes, and
the dataset violates a number of the CFDs given.
3. A set of CFDs on our dataset.

The system would first perform the CFD violating detection, and separates the CFD-violating-tuples 
and inserts them in our test set. We then end up with a clean (non-CFD-violating tuples) set 
that we would use for training our model, and set of violating tuples that we will use for making predictions.

![alt text][System]

## Installation

Make sure the following libraries are in your build path.
```html
guava-18.0.jar
weka-src.jar
weka.jar
```
## Usage
```html
Usage: DTCleaner <input.arff> <CFDinput>
Eample: DTCleaner data/hospitalFewerAttr20PercentNoiseOn4.arff.arff data/CFDs
```

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D


## License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Bibliography
[1] Thomas C. Redman. The impact of poor data quality on the typical enterprise.
Commun. ACM, 41(2):79{82, February 1998.

[System]: http://www.mustafa-s.com/DTCleaner/sys.png
[Logo]: http://www.mustafa-s.com/DTCleaner/DTCleaner.png
