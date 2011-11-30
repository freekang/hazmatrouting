function [] = mysubplot(C, R, C_label, R_label, titlename, filename)
figure()

subplot(2,2,1)
plot3(C(:,1),C(:,2),C(:,3), 'x');
hold all; grid on;
plot3(R(:,1),R(:,2),R(:,3), 'o');
xlabel('cost')
ylabel('total risk')
zlabel('equity risk')


subplot(2,2,2)
plot(C(:,1),C(:,2), 'x');
hold all; grid on;
plot(R(:,1),R(:,2), 'o');
%ylim([4,6]*1e6);
h_legend = legend(C_label, R_label);
set(h_legend,'FontSize',7);
xlabel('cost')
ylabel('total risk')


subplot(2,2,3)
plot(C(:,1),C(:,3), 'x');
hold all; grid on;
plot(R(:,1),R(:,3), 'o');
%ylim([2,5]*1e5);
h_legend = legend(C_label, R_label);
set(h_legend,'FontSize',7);
xlabel('cost')
ylabel('equity risk')


subplot(2,2,4)
plot(C(:,2),C(:,3), 'x');
hold all; grid on;
plot(R(:,2),R(:,3), 'o');
%ylim([2,5]*1e5);
h_legend = legend(C_label, R_label);
set(h_legend,'FontSize',7);
xlabel('total risk')
ylabel('equity risk')

% save plot as eps:
exportfig(gcf, filename);

% add title above all for plot on screen:
set(gcf,'NextPlot','add');
axes;
h = title(titlename);
set(gca,'Visible','off');
set(h,'Visible','on'); 


end
