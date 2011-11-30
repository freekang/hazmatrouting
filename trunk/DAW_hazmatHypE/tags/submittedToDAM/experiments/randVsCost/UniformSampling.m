function S = UniformSampling( nrOfSamples, lower, upper )
    dim = length(upper);

    ex = upper - lower;    
    S = repmat( lower, nrOfSamples, 1 ) + ...
        rand(nrOfSamples, dim )*diag(ex);    

