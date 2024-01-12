<br />
<div align="center">

  <h1 align="center"><b>FXCM API Testing Tool</b></h1>

</div>


<div>
    <h3>Instructions</h3>
    <p>This tool tests three of the FXCM APIs - ForexConnect, Java API and FIX. 

    Performed tasks are:
    - Subscribing for real-time price updates
    - Getting historical data
    - Creating market order
    - Closing position
    - Creating stop entry order
    - Creating limit entry order

Login information and FIX credentials are passed in Main.java file.
Test can be executed with multiple instruments at the same time, they just need to be specified in Main.java file.
The .cfg files needed for using FIX API are automatically generated.

The final output files are available in folder "output". They contain collected price data as well as reports for the created orders.
FIX logs are available in 'appRun' folder.
</p>
</div>
